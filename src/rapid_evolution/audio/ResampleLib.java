package rapid_evolution.audio;

import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.log4j.Logger;

import rapid_evolution.RapidEvolution;

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
public class ResampleLib {
    
    private static Logger log = Logger.getLogger(ResampleLib.class);
    
  public ResampleLib(int num_channels, double ratio) {
    if (high_qual_coeffs == null) {
      try {
        FileReader inputstream = new FileReader("filter_coeffs.txt");
        BufferedReader inputbuffer = new BufferedReader(inputstream);
        String line = inputbuffer.readLine();
        high_qual_coeffs = new double[17088];
        int count = 0;
        while (line != null) {
          high_qual_coeffs[count++] = Double.parseDouble(line);
          line = inputbuffer.readLine();
        }
        inputbuffer.close();
        inputstream.close();
      } catch (Exception e) { log.error("ResampleLib(): error", e); }
    }
    src_state = new SRCD();
    src_state.channels = num_channels;
    SINC_FILTER temp_filter = new SINC_FILTER();
    temp_filter.channels = num_channels;
    temp_filter.coeffs = high_qual_coeffs;
    temp_filter.coeff_half_len = high_qual_coeffs.length - 1;
    temp_filter.index_inc = 128;
    temp_filter.has_diffs = 0;
    temp_filter.coeff_len = high_qual_coeffs.length;
    temp_filter.b_len = 1000 + 2 * (int)(0.5 + temp_filter.coeff_len / (temp_filter.index_inc * 1.0) * SRC_MAX_RATIO);
    temp_filter.b_len *= temp_filter.channels;
    temp_filter.buffer = new float[temp_filter.b_len];
    src_state.private_data = temp_filter;
    src_reset(src_state);
    src_state.last_ratio = ratio ;
  }

  public int frames_used = 0;
  public int frames_generated = 0;
  public float[] processed_frames = null;

  public void processSamples(float[] in_samples) {
    SRC_DATA srcdata = new SRC_DATA();
    srcdata.data_in = in_samples;
    srcdata.input_frames = in_samples.length / src_state.channels;
    srcdata.data_out = new float[(int)(Math.floor(src_state.last_ratio * in_samples.length) + 1)];
    srcdata.output_frames = srcdata.data_out.length;
    srcdata.src_ratio = src_state.last_ratio;
    srcdata.input_frames_used = 0 ;
    srcdata.output_frames_gen = 0 ;
    sinc_process(src_state, srcdata) ;
    frames_used = srcdata.input_frames_used;
    frames_generated = srcdata.output_frames_gen;
    processed_frames = srcdata.data_out;
  }

  private SRCD src_state;

  class SINC_FILTER {
    int	sinc_magic_marker;
    int channels;
    int in_count, in_used;
    int out_count, out_gen;
    int coeff_half_len, index_inc;
    int has_diffs;
    double src_ratio, input_index;
    int	coeff_len;
    double[] coeffs;
    int b_current, b_end, b_real_end, b_len ;
    float[] buffer;
  }

  private void src_reset (SRCD state) {
    SRCD psrc = state;
    sinc_reset(state);
    psrc.last_position = 0.0;
    psrc.last_ratio = 0.0;
  }

  private int SRC_MAX_RATIO = 256;
  private double SRC_MIN_RATIO_DIFF = (1e-20);

  private void sinc_reset (SRCD psrc) {
    SINC_FILTER filter = psrc.private_data;
    filter.b_current = filter.b_end = 0 ;
    filter.b_real_end = -1 ;
    filter.src_ratio = filter.input_index = 0.0 ;
    for (int i = 0; i < filter.b_len; ++i) filter.buffer[i] = 0;
  }

  private void sinc_process (SRCD psrc, SRC_DATA data) {
    SINC_FILTER filter ;
    double input_index, src_ratio, count, float_increment, terminate, rem ;
    long increment, start_filter_index ;
    int half_filter_chan_len, samples_in_hand, ch ;
    filter = psrc.private_data;
    filter.in_count = data.input_frames * filter.channels ;
    filter.out_count = data.output_frames * filter.channels ;
    filter.in_used = filter.out_gen = 0 ;
    src_ratio = psrc.last_ratio ;
    /* Check the sample rate ratio wrt the buffer len. */
    count = (filter.coeff_half_len + 2.0) / filter.index_inc ;
    if (Math.min (psrc.last_ratio, data.src_ratio) < 1.0) count /= Math.min (psrc.last_ratio, data.src_ratio) ;
    /* Maximum coefficientson either side of center point. */
    half_filter_chan_len = filter.channels * ((int) (count) + 1) ;
    input_index = psrc.last_position ;
    float_increment = filter.index_inc ;
    rem = input_index - Math.floor(input_index);
    filter.b_current = (filter.b_current + filter.channels * (int) (input_index - rem)) % filter.b_len ;
    input_index = rem ;
    terminate = 1.0 / src_ratio + 1e-20 ;
    /* Main processing loop. */
    while (filter.out_gen < filter.out_count) {
      /* Need to reload buffer? */
      samples_in_hand = (filter.b_end - filter.b_current + filter.b_len) % filter.b_len ;
      if (samples_in_hand <= half_filter_chan_len) {
        prepare_data (filter, data, half_filter_chan_len) ;
        samples_in_hand = (filter.b_end - filter.b_current + filter.b_len) % filter.b_len ;
        if (samples_in_hand <= half_filter_chan_len) break ;
      }
      /* This is the termination condition. */
      if (filter.b_real_end >= 0) {
        if (filter.b_current + input_index + terminate >= filter.b_real_end)
          break ;
      }
      if (Math.abs (psrc.last_ratio - data.src_ratio) > 1e-10) src_ratio = psrc.last_ratio + filter.out_gen * (data.src_ratio - psrc.last_ratio) / (filter.out_count - 1) ;
      float_increment = filter.index_inc * 1.0 ;
      if (src_ratio < 1.0) float_increment = filter.index_inc * src_ratio ;
      increment = (long) (float_increment) ;
      start_filter_index = (long) (input_index * float_increment) ;
      for (ch = 0 ; ch < filter.channels ; ch++) {
        data.data_out [filter.out_gen] = (float)((float_increment / filter.index_inc) * calc_output (filter, increment, start_filter_index, ch) );
        filter.out_gen ++ ;
      }
      /* Figure out the next index. */
      input_index += 1.0 / src_ratio ;
      rem = input_index - Math.floor(input_index);
      filter.b_current = (filter.b_current + filter.channels * (int) (input_index - rem)) % filter.b_len ;
      input_index = rem ;
    } ;
    psrc.last_position = input_index ;
    /* Save current ratio rather then target ratio. */
    psrc.last_ratio = src_ratio ;
    data.input_frames_used = filter.in_used / filter.channels ;
    data.output_frames_gen = filter.out_gen / filter.channels ;
  }

  private void prepare_data (SINC_FILTER filter, SRC_DATA data, int half_filter_chan_len) {
    int len = 0 ;
    if (filter.b_current == 0) {
      /* Initial state. Set up zeros at the start of the buffer and
       ** then load new data after that.
       */
      len = filter.b_len - 2 * half_filter_chan_len ;
      filter.b_current = filter.b_end = half_filter_chan_len ;
    } else if (filter.b_end + half_filter_chan_len + filter.channels < filter.b_len) {
      /*  Load data at current end position. */
      len = Math.max (filter.b_len - filter.b_current - half_filter_chan_len, 0) ;
    } else {
      /* Move data at end of buffer back to the start of the buffer. */
      len = filter.b_end - filter.b_current ;
      for (int i = 0; i < half_filter_chan_len + len; ++i) filter.buffer[i] = filter.buffer[i + filter.b_current - half_filter_chan_len];
      filter.b_current = half_filter_chan_len ;
      filter.b_end = filter.b_current + len ;
      /* Now load data at current end of buffer. */
      len = Math.max (filter.b_len - filter.b_current - half_filter_chan_len, 0) ;
    }
    len = Math.min (filter.in_count - filter.in_used, len) ;
    len -= (len % filter.channels) ;
    for (int i = 0; i < len; ++i) filter.buffer[i + filter.b_end] = data.data_in[i + filter.in_used];
//    memcpy (filter.buffer + filter.b_end, data.data_in + filter.in_used, len * sizeof (filter.buffer [0])) ;
    filter.b_end += len ;
    filter.in_used += len ;
    if ((filter.in_used == filter.in_count) && (filter.b_end - filter.b_current < 2 * half_filter_chan_len) && (data.end_of_input != 0)) {
      /* Handle the case where all data in the current buffer has been
       ** consumed and this is the last buffer.
       */
      if (filter.b_len - filter.b_end < half_filter_chan_len + 5) {
        /* If necessary, move data down to the start of the buffer. */
        len = filter.b_end - filter.b_current ;
        for (int i = 0; i < (half_filter_chan_len + len); ++i) filter.buffer[i] = filter.buffer[i + filter.b_current - half_filter_chan_len];
//        memmove (filter.buffer, filter.buffer + filter.b_current - half_filter_chan_len, (half_filter_chan_len + len) * sizeof (filter.buffer [0])) ;
        filter.b_current = half_filter_chan_len ;
        filter.b_end = filter.b_current + len ;
      }
      filter.b_real_end = filter.b_end ;
      len = half_filter_chan_len + 5 ;
      for (int i = 0; i < len; ++i) filter.buffer[i + filter.b_end] = 0;
//      memset (filter.buffer + filter.b_end, 0, len * sizeof (filter.buffer [0])) ;
      filter.b_end += len ;
    }
  }

  private double calc_output (SINC_FILTER filter, long increment, long start_filter_index, int ch)
  {	double		fraction, left, right, icoeff ;
          long	filter_index, max_filter_index ;
          int			data_index, coeff_count, indx ;

          /* Convert input parameters into fixed point. */
          max_filter_index = (long) (filter.coeff_half_len) ;

          /* First apply the left half of the filter. */
          filter_index = start_filter_index ;
          coeff_count = (int)((max_filter_index - filter_index) / increment);
          filter_index = filter_index + coeff_count * increment ;
          data_index = filter.b_current - filter.channels * coeff_count ;

          left = 0.0 ;
          do
          {	fraction = (double) (filter_index) ;
                  indx = (int) (filter_index) ;

                  icoeff = filter.coeffs [indx] + fraction * (filter.coeffs [indx + 1] - filter.coeffs [indx]) ;

                  left += icoeff * filter.buffer [data_index + ch] ;

                  filter_index -= increment ;
                  data_index = data_index + filter.channels ;
                  }
          while (filter_index >= (0L)) ;

          /* Now apply the right half of the filter. */
          filter_index = increment - start_filter_index ;
          coeff_count = (int)((max_filter_index - filter_index) / increment );
          filter_index = filter_index + coeff_count * increment ;
          data_index = filter.b_current + filter.channels * (1 + coeff_count) ;

          right = 0.0 ;
          do
          {	fraction = (double) (filter_index) ;
                  indx = (int) (filter_index) ;

                  icoeff = filter.coeffs [indx] + fraction * (filter.coeffs [indx + 1] - filter.coeffs [indx]) ;

                  right += icoeff * filter.buffer [data_index + ch] ;

                  filter_index -= increment ;
                  data_index = data_index - filter.channels ;
                  }
          while (filter_index > (0L)) ;

          return (left + right) ;
  } /* calc_output */

/*
  private double calc_output (SINC_FILTER filter, double increment, double start_filter_index, int ch) {
    double fraction, left, right, icoeff ;
    double filter_index, max_filter_index ;
    int data_index, coeff_count, indx ;
    // Convert input parameters into fixed point.
    max_filter_index = (double) (filter.coeff_half_len) ;
    // First apply the left half of the filter.
    filter_index = start_filter_index ;
    coeff_count = (int)((max_filter_index - filter_index) / increment );
    filter_index = filter_index + coeff_count * increment ;
    data_index = filter.b_current - filter.channels * coeff_count ;
    left = 0.0 ;
    do {
      fraction =  (filter_index) ;
      indx = (int) (filter_index) ;
      icoeff = filter.coeffs [indx] + fraction * (filter.coeffs [indx + 1] - filter.coeffs [indx]) ;
      left += icoeff * filter.buffer [data_index + ch] ;
      filter_index -= increment ;
      data_index = data_index + filter.channels ;
    }
    while (filter_index >= 0.0) ;
    // Now apply the right half of the filter.
    filter_index = increment - start_filter_index ;
    coeff_count = (int)((max_filter_index - filter_index) / increment );
    filter_index = filter_index + coeff_count * increment ;
    data_index = filter.b_current + filter.channels * (1 + coeff_count) ;
    right = 0.0 ;
    do {
      fraction =  (filter_index) ;
      indx = (int) (filter_index) ;
      icoeff = filter.coeffs [indx] + fraction * (filter.coeffs [indx + 1] - filter.coeffs [indx]) ;
      right += icoeff * filter.buffer [data_index + ch] ;
      filter_index -= increment ;
      data_index = data_index - filter.channels ;
    }
    while (filter_index > 0.0) ;
    return (left + right) ;
  }
*/

  class SRC_DATA {
    float[] data_in, data_out;
    int input_frames, output_frames;
    int input_frames_used, output_frames_gen;
    int end_of_input = 0;
    double src_ratio;
  }

  // class to store Sample Rate Conversion process and state data
  class SRCD {
    double last_ratio, last_position;
    int error;
    int channels;
    /* SRC_MODE_PROCESS or SRC_MODE_CALLBACK */
    int	mode;
    /* Pointer to data to converter specific data. */
    SINC_FILTER private_data;
  }

  static double[] high_qual_coeffs = null;
}
