package rapid_evolution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import rapid_evolution.filefilters.ExcelFileFilter;
import rapid_evolution.filefilters.M3UFileFilter;
import rapid_evolution.filefilters.MixMeisterBpmFileFilter;
import rapid_evolution.filefilters.MixMeisterFileFilter;
import rapid_evolution.filefilters.MixVibesFilter;
import rapid_evolution.filefilters.iTunesXMLFileFilter;
import rapid_evolution.ui.ImportMixmeisterUI;
import rapid_evolution.ui.ImportMixvibesUI;
import rapid_evolution.ui.ImportSpreadsheetUI;
import rapid_evolution.ui.RapidEvolutionUI;
import rapid_evolution.ui.SkinManager;

import com.ibm.iwt.IOptionPane;

import rapid_evolution.filefilters.*;

import com.mixshare.rapid_evolution.data.export.NMLExporter;
import com.mixshare.rapid_evolution.data.export.iTunesExporter;

import com.mixshare.rapid_evolution.ui.swing.filechooser.REFileChooser;

public class ImportLib {
    
    private static Logger log = Logger.getLogger(ImportLib.class);
    
    private ImportLib() { }

    public static MixMeisterBpmFileFilter mixMeisterBpmFileFilter = new MixMeisterBpmFileFilter();;
    public static MixVibesFilter mixVibesFilter = new MixVibesFilter();
    
    public static boolean stopimporting;
    public static boolean stopexporting;

    public static void InitiateExport() {
        JFileChooser fc = new REFileChooser();
        stopexporting = false;
        fc.setAcceptAllFileFilterUsed(false);
        if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
        TraktorFileFilter traktorFilter = new TraktorFileFilter();
        fc.addChoosableFileFilter(traktorFilter);
        iTunesXMLFileFilter itunesFilter = new iTunesXMLFileFilter();
        fc.addChoosableFileFilter(itunesFilter);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
        File tmp = fc.getSelectedFile();
        if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filestr = (String) tmp.getAbsolutePath();
            if (filestr != null) {
                log.debug("InitiateExport(): exporting to file=" + filestr);
                if (fc.getFileFilter() == traktorFilter) {
                    if (!filestr.toLowerCase().endsWith(".nml"))
                        filestr += ".nml";
                    new  NMLExporter(filestr, true).start();
                    //NMLExporter.exportDatabaseToTraktor(filestr, false);
                } else if (fc.getFileFilter() == itunesFilter) {
                    int n = IOptionPane.showConfirmDialog(
                            SkinManager.instance.getFrame("main_frame"),
                            "Please read this carefully!  The iTunes export feature is currently experimental, use it at your own risk...\n" +
                            "The only way that has been found to get information in bulk into iTunes is to export an iTunes XML file and then\n" +
                            "purposely corrupt the main iTunes database.  This forces iTunes to recreate its database from the XML file.\n" +
                            "It is possible that some information will be lost in this process!  Only use this feature if absolutely necessary...\n" +
                            "In most cases, information can be transferred to iTunes via other means, such as writing the information to the tags...\n" +
                            "If you wish to continue, you should backup your iTunes ITL database first and also shut down iTunes.\n\n" +
                            "Are you sure you wish to proceed?",
                            "WARNING!",
                            IOptionPane.YES_NO_OPTION);
                        if (n == 0) {                                        
                            if (!filestr.toLowerCase().endsWith(".xml"))
                                filestr += ".xml";
                            new  iTunesExporter(filestr, true).start();                    
                        }
                }
            }
        }        
    }
    
    public static String importfilestr = null;
    public static HSSFWorkbook workbook = null;
    public static HSSFSheet sheet = null;
    public static void InitiateImport() {
      // import data
        stopimporting = false;
        JFileChooser fc = new REFileChooser();
      if (!RapidEvolutionUI.instance.previousfilepath.equals("")) fc.setCurrentDirectory(new File(RapidEvolutionUI.instance.previousfilepath));
      fc.addChoosableFileFilter(new M3UFileFilter());
      iTunesXMLFileFilter iTunesfilter = new iTunesXMLFileFilter();
      fc.addChoosableFileFilter(iTunesfilter);
      TraktorFileFilter traktorFilter = new TraktorFileFilter();
      fc.addChoosableFileFilter(traktorFilter);
      fc.addChoosableFileFilter(new ExcelFileFilter());      
      fc.addChoosableFileFilter(mixVibesFilter);
      fc.addChoosableFileFilter(mixMeisterBpmFileFilter);
      fc.addChoosableFileFilter(new MixMeisterFileFilter());
      fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fc.setMultiSelectionEnabled(false);
      int returnVal = fc.showOpenDialog(SkinManager.instance.getFrame("main_frame"));
      File tmp = fc.getSelectedFile();
      if (tmp != null) RapidEvolutionUI.instance.previousfilepath = tmp.getAbsolutePath();
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        String filestr = (String) tmp.getAbsolutePath();
        log.debug("InitiateImport(): importing file: " + filestr);
        importfilestr = filestr;
        if (fc.getFileFilter() == mixMeisterBpmFileFilter) {
          ImportMixmeisterUI.instance.display_parameter = mixMeisterBpmFileFilter;
          ImportMixmeisterUI.instance.Display();
        } else if (fc.getFileFilter() == mixVibesFilter) {
            ImportMixvibesUI.instance.Display();

            // import mixvibes
            
        } else if (fc.getFileFilter() == iTunesfilter) {
            RapidEvolutionUI.instance.importitunes_ui.display_parameter = importfilestr;
            RapidEvolutionUI.instance.importitunes_ui.Display();
        } else if (fc.getFileFilter() == traktorFilter) {
            RapidEvolutionUI.instance.importtraktor_ui.display_parameter = importfilestr;
            RapidEvolutionUI.instance.importtraktor_ui.Display();
        } else if (filestr.toLowerCase().endsWith(".m3u")) {
          ImportM3U(filestr);
        } else if (filestr.toLowerCase().endsWith(".xls")) {
          try {
              log.debug("InitiateImport(): reading excel spreadsheet...");
            workbook = new HSSFWorkbook(new FileInputStream(filestr));
            sheet = workbook.getSheetAt(0);
            HSSFRow row = sheet.getRow(0);
            ImportSpreadsheetUI.instance.importexcelartistcombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelalbumcombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceltrackcombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceltimecombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceltimesigcombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceltitlecombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelremixercombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelstartbpmcombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelendbpmcombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelstartkeycombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelendkeycombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelcommentscombo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceluser1combo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceluser2combo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceluser3combo.removeAllItems();
            ImportSpreadsheetUI.instance.importexceluser4combo.removeAllItems();
            ImportSpreadsheetUI.instance.importexcelfilenamecombo.removeAllItems();

            ImportSpreadsheetUI.instance.importexcelartistcombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelalbumcombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceltrackcombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceltimecombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceltimesigcombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceltitlecombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelremixercombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelstartbpmcombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelendbpmcombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelstartkeycombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelendkeycombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelcommentscombo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceluser1combo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceluser2combo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceluser3combo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexceluser4combo.addItem("<none>");
            ImportSpreadsheetUI.instance.importexcelfilenamecombo.addItem("<none>");
            for (short s = row.getFirstCellNum(); s < row.getLastCellNum(); ++s) {
              HSSFCell cell = row.getCell(s);
              ImportSpreadsheetUI.instance.importexcelartistcombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelalbumcombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceltrackcombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceltimecombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceltimesigcombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceltitlecombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelremixercombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelstartbpmcombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelendbpmcombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelstartkeycombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelendkeycombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelcommentscombo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceluser1combo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceluser2combo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceluser3combo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexceluser4combo.addItem(cell.getStringCellValue());
              ImportSpreadsheetUI.instance.importexcelfilenamecombo.addItem(cell.getStringCellValue());
            }
            ImportSpreadsheetUI.instance.Display();
          } catch (Exception e) { log.error("InitiateImport(): error", e);
          IOptionPane.showMessageDialog(SkinManager.instance.getFrame("main_frame"),
              SkinManager.instance.getDialogMessageText("error_reading_spreadsheet"),
              SkinManager.instance.getDialogMessageTitle("error_reading_spreadsheet"),
              IOptionPane.ERROR_MESSAGE);
          }
        } else {
            ImportMixmeisterUI.instance.Display();
        }
      }
    }

    public static void ImportM3U(String filestr) {
      File m3ufile = new File(filestr);
      try {
      Vector filevector = new Vector();
      FileReader inputstream = new FileReader(filestr);
      BufferedReader inputbuffer = new BufferedReader(inputstream);
      String line = inputbuffer.readLine();
      while (line != null) {
          if (!line.startsWith("#EXTINF")) {
            File file = new File(line);
            if (file.isFile()) filevector.add(file);
            else {
              int index = m3ufile.getPath().indexOf(m3ufile.getName());
              String try2 = m3ufile.getPath().substring(0, index) + line;
              file = new File(try2);
              if (file.isFile()) filevector.add(file);
            }
          }
          line = inputbuffer.readLine();
      }
      inputbuffer.close();
      inputstream.close();
      if (filevector.size() > 0) {
        File[] files = new File[filevector.size()];
        for (int i = 0; i < filevector.size(); ++i) {
          files[i] = (File)filevector.get(i);
        }

        RapidEvolutionUI.instance.AcceptBrowseAddSongs(files, true);
      }
      } catch (Exception e) { log.error("ImportM3U(): error", e); }
    }

    
}
