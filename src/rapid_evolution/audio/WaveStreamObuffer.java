package rapid_evolution.audio;

import rapid_evolution.RapidEvolution;
import javazoom.jl.decoder.Obuffer;

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

public class WaveStreamObuffer extends Obuffer {
        private short[] buffer;		// stores decoded frame
        private short[] bufferp;
        private int channels;

        public WaveStreamObuffer(int number_of_channels, int freq) {
                buffer = new short[OBUFFERSIZE];
                bufferp = new short[MAXCHANNELS];
                channels = number_of_channels;
                for(int i=0;i<channels;++i) bufferp[i] = (short)i;
        }

        /**
         * append
         *
         * - append a value to the channel buffer
         *
         */
        public void append(int channel, short value) {
                buffer[bufferp[channel]] = value;
                bufferp[channel] += channels;
        }

        /**
         * write_buffer
         *
         * - write a value to the buffer
         *
         */
        public void write_buffer(int val) {
                for(int i=0;i<channels;++i) bufferp[i] = (short)i;
        }

        public byte [] get_data() {
        /**
         * get_data
         *
         * - Used by Player to retrieve the output written to the Obuffer
         *   by the Decoder. The bytes are retrieved and sent directly to
         *   the SourceDataLine for playing.
         */
                byte[] theData = new byte[buffer.length*2];
                int yc = 0;
                for (int y = 0;y<buffer.length*2;y=y+2) {
                        theData[y] = (byte) (buffer[yc] & 0x00FF);
                        theData[y+1] =(byte) ((buffer[yc++] >>> 8) & 0x00FF);
                }
                return theData;
        }

        // close() code removed - not needed
        public void close() {}

        // clear_buffer() code removed - not needed
        public void clear_buffer() {}

        // set_stop_flag() code removed - not needed
        public void set_stop_flag() {}
}
