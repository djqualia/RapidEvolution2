package rapid_evolution.audio;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;
import rapid_evolution.util.intHolder;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Resampler {
    
    private static Logger log = Logger.getLogger(Resampler.class);
    
  public Resampler(int numchannels, double ratio) {
    processor = new rsdata[numchannels];
    for (int i = 0; i < processor.length; ++i) processor[i] = resample_open(1, ratio, ratio);
  }

  public rsdata[] processor;

  /* Accuracy */
  int Npc = 4096;

  double PI = 3.14159265358979232846;
  double PI2 = 6.28318530717958465692;
  double D2R = 0.01745329348;          /* (2*pi)/360 */
  double R2D = 57.29577951;            /* 360/(2*pi) */

  int SGN(int x) {
    if (x < 0) return -1;
    else if (x > 0) return 1;
    else return 0;
  }

  class rsdata {
    public float[] Imp;
    public float[] ImpD;
    public float LpScl;
    public int Nmult;
    public int Nwing;
    public double minFactor;
    public double maxFactor;
    public int XSize;
    public float[] X;
    public int Xp; /* Current "now"-sample pointer for input */
    public int Xread; /* Position to put new samples */
    public int Xoff;
    public int YSize;
    public float[] Y;
    public int Yp;
    public double Time;
  }

  rsdata resample_dup(rsdata handle) {
     rsdata cpy = handle;
     rsdata hp = new rsdata();

     hp.minFactor = cpy.minFactor;
     hp.maxFactor = cpy.maxFactor;
     hp.Nmult = cpy.Nmult;
     hp.LpScl = cpy.LpScl;
     hp.Nwing = cpy.Nwing;

     hp.Imp = new float[hp.Nwing]; //(float *)malloc(hp->Nwing * sizeof(float));
     for (int i = 0; i < hp.Imp.length; ++i) hp.Imp[i] = cpy.Imp[i];
//     memcpy(hp.Imp, cpy.Imp, hp.Nwing * sizeof(float));
     hp.ImpD = new float[hp.Nwing]; //(float *)malloc(hp.Nwing * sizeof(float));
     for (int i = 0; i < hp.ImpD.length; ++i) hp.ImpD[i] = cpy.ImpD[i];
//     memcpy(hp.ImpD, cpy.ImpD, hp.Nwing * sizeof(float));

     hp.Xoff = cpy.Xoff;
     hp.XSize = cpy.XSize;
     hp.X = new float[hp.XSize + hp.Xoff]; //(float *)malloc((hp.XSize + hp.Xoff) * sizeof(float));
     for (int i = 0; i < hp.X.length; ++i) hp.X[i] = cpy.X[i];
//     memcpy(hp.X, cpy.X, (hp.XSize + hp.Xoff) * sizeof(float));
     hp.Xp = cpy.Xp;
     hp.Xread = cpy.Xread;
     hp.YSize = cpy.YSize;
     hp.Y = new float[hp.YSize]; //(float *)malloc(hp.YSize * sizeof(float));
     for (int i = 0; i < hp.Y.length; ++i) hp.Y[i] = cpy.Y[i];
//     memcpy(hp.Y, cpy.Y, hp.YSize * sizeof(float));
     hp.Yp = cpy.Yp;
     hp.Time = cpy.Time;

     return hp;
  }

  rsdata resample_open(int highQuality, double minFactor, double maxFactor)
  {
     double[] Imp64;
     double Rolloff, Beta;
     rsdata hp = new rsdata();;
     int Xoff_min, Xoff_max;
     int i;

     /* Just exit if we get invalid factors */
     if (minFactor <= 0.0 || maxFactor <= 0.0 || maxFactor < minFactor) {
//       log.debug("resample_open(): minFactor and maxFactor must be positive real numbers and maxFactor should be larger than minFactor.");
        return null;
     }

     hp.minFactor = minFactor;
     hp.maxFactor = maxFactor;

     if (highQuality != 0)
        hp.Nmult = 35;
     else
        hp.Nmult = 11;

     hp.LpScl = 1.0f;
     hp.Nwing = Npc*(hp.Nmult-1)/2; /* # of filter coeffs in right wing */

     Rolloff = 0.90;
     Beta = 6;

     Imp64 = new double[hp.Nwing]; //(double *)malloc(hp->Nwing * sizeof(double));

     lrsLpFilter(Imp64, hp.Nwing, 0.5*Rolloff, Beta, Npc);

     hp.Imp = new float[hp.Nwing]; //(float *)malloc(hp->Nwing * sizeof(float));
     hp.ImpD = new float[hp.Nwing]; //(float *)malloc(hp->Nwing * sizeof(float));
     for(i=0; i<hp.Nwing; i++)
        hp.Imp[i] = (float)Imp64[i];

     /* Storing deltas in ImpD makes linear interpolation
        of the filter coefficients faster */
     for (i=0; i<hp.Nwing-1; i++)
        hp.ImpD[i] = hp.Imp[i+1] - hp.Imp[i];

     /* Last coeff. not interpolated */
     hp.ImpD[hp.Nwing-1] = - hp.Imp[hp.Nwing-1];

     Imp64 = null;
//     free(Imp64);

     /* Calc reach of LP filter wing (plus some creeping room) */
     Xoff_min = (int)(((hp.Nmult+1)/2.0) * Math.max(1.0, 1.0/minFactor) + 10);
     Xoff_max = (int)(((hp.Nmult+1)/2.0) * Math.max(1.0, 1.0/maxFactor) + 10);
     hp.Xoff = Math.max(Xoff_min, Xoff_max);

     /* Make the inBuffer size at least 4096, but larger if necessary
        in order to store the minimum reach of the LP filter and then some.
        Then allocate the buffer an extra Xoff larger so that
        we can zero-pad up to Xoff zeros at the end when we reach the
        end of the input samples. */
     hp.XSize = Math.max(2*hp.Xoff+10, 4096);
     hp.X = new float[hp.XSize + hp.Xoff]; //(float *)malloc((hp->XSize + hp->Xoff) * sizeof(float));
     hp.Xp = hp.Xoff;
     hp.Xread = hp.Xoff;

     /* Need Xoff zeros at begining of X buffer */
     for(i=0; i<hp.Xoff; i++)
        hp.X[i]=0;

     /* Make the outBuffer long enough to hold the entire processed
        output of one inBuffer */
     hp.YSize = (int)(((double)hp.XSize)*maxFactor+2.0);
     hp.Y = new float[hp.YSize]; //(float *)malloc(hp->YSize * sizeof(float));
     hp.Yp = 0;

     hp.Time = (double)hp.Xoff; /* Current-time pointer for converter */

     return hp;
  }

  int resample_get_filter_width(rsdata handle) {
     return handle.Xoff;
  }

  int resample_process(rsdata handle,
                       double  factor,
                       float[] inBuffer,
                       int     inBufferLen,
                       int     lastFlag,
                       intHolder   inBufferUsed, /* output param */
                       float[] outBuffer,
                       int     outBufferLen)
  {
     rsdata hp = handle;
     float[] Imp = hp.Imp;
     float[] ImpD = hp.ImpD;
     float  LpScl = hp.LpScl;
     int  Nwing = hp.Nwing;
     boolean interpFilt = false; /* TRUE means interpolate filter coeffs */
     int outSampleCount;
     int Nout, Ncreep, Nreuse;
     int Nx;
     int i, len;

//     if (RapidEvolution.debugmode) System.out.println("resample_process: in=%d, out=%d lastFlag=%d\n",
  //           inBufferLen, outBufferLen, lastFlag);

     /* Initialize inBufferUsed and outSampleCount to 0 */
     inBufferUsed.value = 0;
     outSampleCount = 0;

     if (factor < hp.minFactor || factor > hp.maxFactor) {
//        fprintf(stderr,
//                "libresample: factor %f is not between "
//                "minFactor=%f and maxFactor=%f",
//                factor, hp.minFactor, hp.maxFactor);
        return -1;
     }

     /* Start by copying any samples still in the Y buffer to the output
        buffer */
     if ((hp.Yp != 0) && ((outBufferLen-outSampleCount)>0)) {
        len = Math.min(outBufferLen-outSampleCount, hp.Yp);
        for(i=0; i<len; i++)
           outBuffer[outSampleCount+i] = hp.Y[i];
        outSampleCount += len;
        for(i=0; i<hp.Yp-len; i++)
           hp.Y[i] = hp.Y[i+len];
        hp.Yp -= len;
     }

     /* If there are still output samples left, return now - we need
        the full output buffer available to us... */
     if (hp.Yp != 0)
        return outSampleCount;

     /* Account for increased filter gain when using factors less than 1 */
     if (factor < 1)
        LpScl = (float)(LpScl*factor);

     for(;;) {

        /* This is the maximum number of samples we can process
           per loop iteration */

//        if (RapidEvolution.debugmode) {
//        printf("XSize: %d Xoff: %d Xread: %d Xp: %d lastFlag: %d\n",
//               hp.XSize, hp.Xoff, hp.Xread, hp.Xp, lastFlag);
//        }

        /* Copy as many samples as we can from the input buffer into X */
        len = hp.XSize - hp.Xread;

        if (len >= (inBufferLen - inBufferUsed.value))
           len = (inBufferLen - inBufferUsed.value);

        for(i=0; i<len; i++)
           hp.X[hp.Xread + i] = inBuffer[inBufferUsed.value + i];

        inBufferUsed.value += len;
        hp.Xread += len;

        if ((lastFlag != 0) && (inBufferUsed.value == inBufferLen)) {
           /* If these are the last samples, zero-pad the
              end of the input buffer and make sure we process
              all the way to the end */
           Nx = hp.Xread - hp.Xoff;
           for(i=0; i<hp.Xoff; i++)
              hp.X[hp.Xread + i] = 0;
        }
        else
           Nx = hp.Xread - 2 * hp.Xoff;

//      if (RapidEvolution.debugmode) { fprintf(stderr, "new len=%d Nx=%d\n", len, Nx); }

        if (Nx <= 0)
           break;

        /* Resample stuff in input buffer */
        if (factor >= 1) {      /* SrcUp() is faster if we can use it */
          doubleHolder holder = new doubleHolder(hp.Time);
           Nout = lrsSrcUp(hp.X, hp.Y, 0, factor, holder, Nx,
                           Nwing, LpScl, Imp, ImpD, interpFilt);
           hp.Time = holder.value;
        }
        else {
          doubleHolder holder = new doubleHolder(hp.Time);
           Nout = lrsSrcUD(hp.X, hp.Y, 0, factor, holder, Nx,
                           Nwing, LpScl, Imp, ImpD, interpFilt);
           hp.Time = holder.value;
        }


//        if (RapidEvolution.debugmode) printf("Nout: %d\n", Nout);

        hp.Time -= Nx;         /* Move converter Nx samples back in time */
        hp.Xp += Nx;           /* Advance by number of samples processed */

        /* Calc time accumulation in Time */
        Ncreep = (int)(hp.Time) - hp.Xoff;
        if (Ncreep != 0) {
           hp.Time -= Ncreep;  /* Remove time accumulation */
           hp.Xp += Ncreep;    /* and add it to read pointer */
        }

        /* Copy part of input signal that must be re-used */
        Nreuse = hp.Xread - (hp.Xp - hp.Xoff);

        for (i=0; i<Nreuse; i++)
           hp.X[i] = hp.X[i + (hp.Xp - hp.Xoff)];

//        if (RapidEvolution.debugmode) printf("New Xread=%d\n", Nreuse);

        hp.Xread = Nreuse;  /* Pos in input buff to read new data into */
        hp.Xp = hp.Xoff;

        /* Check to see if output buff overflowed (shouldn't happen!) */
        if (Nout > hp.YSize) {
//          if (RapidEvolution.debugmode) printf("Nout: %d YSize: %d\n", Nout, hp.YSize);
//           fprintf(stderr, "libresample: Output array overflow!\n");
           return -1;
        }

        hp.Yp = Nout;

        /* Copy as many samples as possible to the output buffer */
        if ((hp.Yp != 0) && (outBufferLen-outSampleCount)>0) {
           len = Math.min(outBufferLen-outSampleCount, hp.Yp);
           for(i=0; i<len; i++)
              outBuffer[outSampleCount+i] = hp.Y[i];
           outSampleCount += len;
           for(i=0; i<hp.Yp-len; i++)
              hp.Y[i] = hp.Y[i+len];
           hp.Yp -= len;
        }

        /* If there are still output samples left, return now,
           since we need the full output buffer available */
        if (hp.Yp != 0)
           break;
     }

     return outSampleCount;
  }

  void resample_close(rsdata handle) {
     rsdata hp = handle;
     hp.X = null;
     hp.Y = null;
     hp.Imp = null;
     hp.ImpD = null;
//     free(hp);
  }

  class doubleHolder {
    public doubleHolder(double doubleval) { value = doubleval; }
    public double value;
  }

  int lrsSrcUp(float X[],
               float Y[], int Yi,
               double factor,
               doubleHolder TimePtr,
               int Nx,
               int Nwing,
               float LpScl,
               float Imp[],
               float ImpD[],
               boolean Interp)
  {
    int Xp;
    int Ystart;
      float v;

      double CurrentTime = TimePtr.value;
      double dt;                 /* Step through input signal */
      double endTime;            /* When Time reaches EndTime, return to user */

      dt = 1.0/factor;           /* Output sampling period */

      Ystart = Yi;
      endTime = CurrentTime + Nx;
      while (CurrentTime < endTime)
      {
          double LeftPhase = CurrentTime-Math.floor(CurrentTime);
          double RightPhase = 1.0 - LeftPhase;

          Xp = (int)CurrentTime; /* Ptr to current input sample */
          /* Perform left-wing inner product */
          v = lrsFilterUp(Imp, ImpD, Nwing, Interp, X, Xp,
                          LeftPhase, -1);
          /* Perform right-wing inner product */
          v += lrsFilterUp(Imp, ImpD, Nwing, Interp, X, Xp+1,
                           RightPhase, 1);

          v *= LpScl;   /* Normalize for unity filter gain */

          Y[Yi++] = v;
//          *Y++ = v;               /* Deposit output */
          CurrentTime += dt;      /* Move to next sample by time increment */
      }

      TimePtr.value = CurrentTime;
      return (Yi - Ystart);        /* Return the number of output samples */
  }

  /* Sampling rate conversion subroutine */

  int lrsSrcUD(float X[],
               float Y[], int Yi,
               double factor,
               doubleHolder TimePtr,
               int Nx,
               int Nwing,
               float LpScl,
               float Imp[],
               float ImpD[],
               boolean Interp)
  {
      int Xp, Ystart;
      float v;

      double CurrentTime = TimePtr.value;
      double dh;                 /* Step through filter impulse response */
      double dt;                 /* Step through input signal */
      double endTime;            /* When Time reaches EndTime, return to user */

      dt = 1.0/factor;            /* Output sampling period */

      dh = Math.min(Npc, factor*Npc);  /* Filter sampling period */

      Ystart = Yi;
      endTime = CurrentTime + Nx;
      while (CurrentTime < endTime)
      {
          double LeftPhase = CurrentTime-Math.floor(CurrentTime);
          double RightPhase = 1.0 - LeftPhase;

          Xp = (int)CurrentTime;     /* Ptr to current input sample */
          /* Perform left-wing inner product */
          v = lrsFilterUD(Imp, ImpD, Nwing, Interp, X, Xp,
                          LeftPhase, -1, dh);
          /* Perform right-wing inner product */
          v += lrsFilterUD(Imp, ImpD, Nwing, Interp, X, Xp+1,
                           RightPhase, 1, dh);

          v *= LpScl;   /* Normalize for unity filter gain */
          Y[Yi++] = v;
//          *Y++ = v;               /* Deposit output */

          CurrentTime += dt;      /* Move to next sample by time increment */
      }

      TimePtr.value = CurrentTime;
      return (Yi - Ystart);        /* Return the number of output samples */
  }

  static double IzeroEPSILON = 1E-21;

  static double Izero(double x)
  {
     double sum, u, halfx, temp;
     int n;

     sum = u = n = 1;
     halfx = x/2.0;
     do {
        temp = halfx/(double)n;
        n += 1;
        temp *= temp;
        u *= temp;
        sum += u;
     } while (u >= IzeroEPSILON*sum);
     return(sum);
  }

  void lrsLpFilter(double c[], int N, double frq, double Beta, int Num)
  {
     double IBeta, temp, temp1, inm1;
     int i;

     /* Calculate ideal lowpass filter impulse response coefficients: */
     c[0] = 2.0*frq;
     for (i=1; i<N; i++) {
        temp = PI*(double)i/(double)Num;
        c[i] = Math.sin(2.0*temp*frq)/temp; /* Analog sinc function, cutoff = frq */
     }

     /*
      * Calculate and Apply Kaiser window to ideal lowpass filter.
      * Note: last window value is IBeta which is NOT zero.
      * You're supposed to really truncate the window here, not ramp
      * it to zero. This helps reduce the first sidelobe.
      */
     IBeta = 1.0/Izero(Beta);
     inm1 = 1.0/((double)(N-1));
     for (i=1; i<N; i++) {
        temp = (double)i * inm1;
        temp1 = 1.0 - temp*temp;
        temp1 = (temp1<0? 0: temp1); /* make sure it's not negative since
                                        we're taking the square root - this
                                        happens on Pentium 4's due to tiny
                                        roundoff errors */
        c[i] *= Izero(Beta*Math.sqrt(temp1)) * IBeta;
     }
  }

  float lrsFilterUp(float Imp[],  /* impulse response */
                    float ImpD[], /* impulse response deltas */
                    int Nwing,  /* len of one wing of filter */
                    boolean Interp,  /* Interpolate coefs using deltas? */
                    float[] Xp, int Xpi,    /* Current sample */
                    double Ph,    /* Phase */
                    int Inc)    /* increment (1 for right wing or -1 for left) */
  {
    int Hp, Hdp = 0, End;
//     float *Hp, *Hdp = NULL, *End;
     double a = 0;
     float v, t;

     Ph *= Npc; /* Npc is number of values per 1/delta in impulse response */

     v = 0.0f; /* The output value */
     Hp = (int)Ph;
     End = Nwing;
     if (Interp) {
        Hdp = (int)Ph;
        a = Ph - Math.floor(Ph); /* fractional part of Phase */
     }

     if (Inc == 1)		/* If doing right wing...              */
     {				      /* ...drop extra coeff, so when Ph is  */
        End--;			/*    0.5, we don't do too many mult's */
        if (Ph == 0)		/* If the phase is zero...           */
        {			         /* ...then we've already skipped the */
           Hp += Npc;		/*    first sample, so we must also  */
           Hdp += Npc;		/*    skip ahead in Imp[] and ImpD[] */
        }
     }

     if (Interp)
        while (Hp < End) {
           t = Imp[Hp];		/* Get filter coeff */
           t += ImpD[Hdp]*a; /* t is now interp'd filter coeff */
           Hdp += Npc;		/* Filter coeff differences step */
           t *= Xp[Xpi];		/* Mult coeff by input sample */
           v += t;			/* The filter output */
           Hp += Npc;		/* Filter coeff step */
           Xpi += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
        }
     else
        while (Hp < End) {
           t = Imp[Hp];		/* Get filter coeff */
           t *= Xp[Xpi];		/* Mult coeff by input sample */
           v += t;			/* The filter output */
           Hp += Npc;		/* Filter coeff step */
           Xpi += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
        }

     return v;
  }

  float lrsFilterUD(float Imp[],  /* impulse response */
                    float ImpD[], /* impulse response deltas */
                    int Nwing,  /* len of one wing of filter */
                    boolean Interp,  /* Interpolate coefs using deltas? */
                    float[] Xp, int Xpi,    /* Current sample */
                    double Ph,    /* Phase */
                    int Inc,    /* increment (1 for right wing or -1 for left) */
                    double dhb) /* filter sampling period */
  {
     float a;
     int Hp, Hdp = 0, End;
//     float *Hp, *Hdp, *End;
     float v, t;
     double Ho;

     v = 0.0f; /* The output value */
     Ho = Ph*dhb;
     End = Nwing;
     if (Inc == 1)		/* If doing right wing...              */
     {				      /* ...drop extra coeff, so when Ph is  */
        End--;			/*    0.5, we don't do too many mult's */
        if (Ph == 0)		/* If the phase is zero...           */
           Ho += dhb;		/* ...then we've already skipped the */
     }				         /*    first sample, so we must also  */
                          /*    skip ahead in Imp[] and ImpD[] */

     if (Interp)
        while ((Hp = (int)Ho) < End) {
           t = Imp[Hp];		/* Get IR sample */
           Hdp = (int)Ho;  /* get interp bits from diff table*/
           a = (float)(Ho - Math.floor(Ho));	  /* a is logically between 0 and 1 */
           t += ImpD[Hdp]*a; /* t is now interp'd filter coeff */
           t *= Xp[Xpi];		/* Mult coeff by input sample */
           v += t;			/* The filter output */
           Ho += dhb;		/* IR step */
           Xpi += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
        }
     else
        while ((Hp = (int)Ho) < End) {
           t = Imp[Hp];		/* Get IR sample */
           t *= Xp[Xpi];		/* Mult coeff by input sample */
           v += t;			/* The filter output */
           Ho += dhb;		/* IR step */
           Xpi += Inc;		/* Input signal step. NO CHECK ON BOUNDS */
        }

     return v;
  }

}

