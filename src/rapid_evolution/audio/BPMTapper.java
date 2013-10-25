package rapid_evolution.audio;

import rapid_evolution.ui.main.SearchPane;
import rapid_evolution.SongUtil;
import rapid_evolution.threads.UpdateThread;
import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.OptionsUI;
import rapid_evolution.ui.AddSongsUI;
import rapid_evolution.ui.EditSongUI;
import rapid_evolution.ui.RapidEvolutionUI;
import com.mixshare.rapid_evolution.music.Key;

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
public class BPMTapper {
    public BPMTapper() {
      instance = this;
      // bpm tapper configuration
      m_threshold = .05;
      m_tolerance = .25;
      m_stablize = 8;
      m_bRegulate = true;
      m_regulationcutoff = 4;
      m_beatmultiplierround = 0.2;
      m_beatdividerround = 0.1;
      m_reset = 5;
      m_bBpmSmartRound = true;
      m_responsedeviation = 0.01;
      m_numTicks = -1;
    }

    public static BPMTapper instance = null;
    // bpm tapper
    public int m_numTicks;
    double m_timeTotal, m_timeMark;
    double m_threshold;
    double m_tolerance;
    int m_stablize;
    boolean m_bRegulate;
    int m_regulationcutoff;
    double m_beatmultiplierround;
    double m_beatdividerround;
    int m_reset;
    boolean m_bBpmSmartRound;
    double m_responsedeviation;
    public boolean maintapped = false;
    public boolean addsongstartbpm1tapped = false;
    public boolean addsongendbpm1tapped = false;
    public boolean editsongstartbpm1tapped = false;
    public boolean editsongendbpm1tapped = false;

    public void TapBPM() {
      double timerValue = ((double)System.currentTimeMillis()) / 1000.0;
      double beatTime, avgTime, multiplier, divider, factor = 0.0;
      beatTime = timerValue - m_timeMark;
      if (m_numTicks == -1) {
        // initialize measurement variables
        m_numTicks = 0;
        m_timeTotal = 0;
      } else if (m_numTicks == 0) {
        // got first beat ...
        m_numTicks++;
        m_timeTotal += beatTime;
      } else if (!(beatTime < m_threshold)) {
        boolean detectbeat = true;
        while (detectbeat) {
          detectbeat = false;
          avgTime = m_timeTotal / m_numTicks;
          if ((Math.abs(beatTime - avgTime) / avgTime) <= m_tolerance) {
            m_timeTotal += beatTime;
            m_numTicks++;
            ShowBPM(avgTime);
          } else if ((Math.abs(beatTime - avgTime) / avgTime) >= m_reset) {
            m_numTicks = -1;
            ResetBpm();
          } else if (m_bRegulate && (m_numTicks >= m_stablize)) {
            multiplier = avgTime / beatTime;
            divider = beatTime / avgTime;
            if ((Math.round(multiplier) <= m_regulationcutoff) && (Math.round(divider) <= m_regulationcutoff)) {
              if (Math.abs(Math.round(multiplier) - multiplier) < m_beatmultiplierround) {
                // regulation beat to multiplier
                beatTime *= Math.round( multiplier );
                factor = multiplier;
                detectbeat = true;
              } else if (Math.abs( Math.round(divider) - divider ) < m_beatdividerround) {
                // regulate beat to divider
                beatTime /= Math.round( divider );
                factor = divider;
                detectbeat = true;
              }
            }
          }
        }
      }
      m_timeMark = timerValue;
    }

    void ShowBPM(double time) {
      if (m_numTicks >= m_stablize) {
        if (maintapped) {
          RapidEvolution.instance.setActualBpm((float) RoundBPM(time));
          if (RapidEvolutionUI.instance.currentsong != null) {
            float usebpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
            if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) usebpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
            if (!OptionsUI.instance.lockpitchshift.isSelected()) {
              SearchPane.instance.bpmslider.setValue( (int) ( (double) SongUtil.get_bpmdiff(RapidEvolution.instance.getActualBpm(), usebpm) *
                                          100.0));
              SearchPane.instance.showSliderShiftValue();
            }
            if (RapidEvolutionUI.instance.currentsong.getStartKey().isValid() ||
                RapidEvolutionUI.instance.currentsong.getEndKey().isValid()) {
              if ( (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) || (RapidEvolutionUI.instance.currentsong.getStartbpm() != 0)) {
                Key fromkey = RapidEvolutionUI.instance.currentsong.getStartKey();
                if (RapidEvolutionUI.instance.currentsong.getEndKey().isValid()) fromkey = RapidEvolutionUI.instance.currentsong.getEndKey();
                float frombpm = RapidEvolutionUI.instance.currentsong.getStartbpm();
                if (RapidEvolutionUI.instance.currentsong.getEndbpm() != 0) frombpm = RapidEvolutionUI.instance.currentsong.getEndbpm();
                float fromdiff = SongUtil.get_bpmdiff(frombpm, RapidEvolution.instance.getActualBpm());
                if (!RapidEvolutionUI.instance.keylockcurrentsong.isSelected())
                    RapidEvolutionUI.instance.setCurrentKey(fromkey.getShiftedKeyByBpmDifference(fromdiff));
                else
                    RapidEvolutionUI.instance.setCurrentKey(fromkey);
                    
                SearchPane.instance.keyfield.setText(RapidEvolutionUI.instance.getCurrentKey().toString());
                SearchPane.instance.searchtable.repaint();
              }
            }
          }
          String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
          int length = bpmtext.length();
          if (length > 6) length = 6;
          SearchPane.instance.bpmfield.setText(bpmtext.substring(0, length));
          SearchPane.instance.bpmsearchbutton.setEnabled(true);
          if ( (RapidEvolutionUI.instance.currentsong != null) &&
              (RapidEvolutionUI.instance.currentsong.getStartKey().isValid() ||
               RapidEvolutionUI.instance.currentsong.getEndKey().isValid())) SearchPane.instance.keysearchbutton.setEnabled(true);
          UpdateThread ut = new UpdateThread();
          ut.start();
        } else if (addsongstartbpm1tapped) {
          String bpmtext = String.valueOf((float) RoundBPM(time));
          int length = bpmtext.length();
          if (length > 6) length = 6;
          AddSongsUI.instance.addsongsstartbpmfield.setText(bpmtext.substring(0, length));
        } else if (addsongendbpm1tapped) {
          String bpmtext = String.valueOf((float) RoundBPM(time));
          int length = bpmtext.length();
          if (length > 6) length = 6;
          AddSongsUI.instance.addsongsendbpmfield.setText(bpmtext.substring(0, length));
        } else if (editsongstartbpm1tapped) {
          String bpmtext = String.valueOf((float) RoundBPM(time));
          int length = bpmtext.length();
          if (length > 6) length = 6;
          EditSongUI.instance.editsongsstartbpmfield.setText(bpmtext.substring(0, length));
        } else if (editsongendbpm1tapped) {
          String bpmtext = String.valueOf((float) RoundBPM(time));
          int length = bpmtext.length();
          if (length > 6) length = 6;
          EditSongUI.instance.editsongsendbpmfield.setText(bpmtext.substring(0, length));
        }
      }
    }

    double RoundBPM(double time) {
      double rounder;
      double bpm = 60.0 / time;
      if (m_bBpmSmartRound) {
        rounder = m_responsedeviation / Math.sqrt((double) m_numTicks) * bpm * bpm / 60.0;
        if (rounder < 0.1) rounder = 0.1;
        else if (rounder < 0.2)	rounder = 0.2;
        else if (rounder < 0.5) rounder = 0.5;
        else if (rounder < 1) rounder = 1;
        else rounder = 2;
      } else rounder = 0.1;
      return Math.round( bpm / rounder ) * rounder;
    }

    public void ResetBpm() {
//    RapidEvolutionUI.instance.actualbpm = ( ( (float) - bpmslider.getValue() / 10000.0f) + 1.0f) * currentbpm;
      if (maintapped) {
        if ( (RapidEvolution.instance.getActualBpm() != 0) && (RapidEvolutionUI.instance.currentsong != null)) {
          String bpmtext = String.valueOf(RapidEvolution.instance.getActualBpm());
          int length = bpmtext.length();
          if (length > 6) length = 6;
          SearchPane.instance.bpmfield.setText(bpmtext.substring(0, length));
        }
        else {
          SearchPane.instance.bpmfield.setText("");
          RapidEvolution.instance.setCurrentBpm(0);
          RapidEvolution.instance.setActualBpm(0);
          SearchPane.instance.bpmsearchbutton.setEnabled(false);
        }
      } else if (addsongstartbpm1tapped) {
        AddSongsUI.instance.addsongsstartbpmfield.setText("");
      } else if (addsongendbpm1tapped) {
        AddSongsUI.instance.addsongsendbpmfield.setText("");
      } else if (editsongstartbpm1tapped) {
        EditSongUI.instance.editsongsstartbpmfield.setText("");
      } else if (editsongendbpm1tapped) {
        EditSongUI.instance.editsongsendbpmfield.setText("");
      }
    }

}
