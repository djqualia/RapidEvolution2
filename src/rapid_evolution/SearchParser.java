package rapid_evolution;

import java.util.Vector;

public class SearchParser {
    public SearchParser(String input) {
        num_rows = 1;
        data = new Vector();
        Vector row = new Vector();
        int index = 0;
        int lastletter = 0;
        boolean insidequotes = false;
        while (index < input.length()) {
            if ((input.charAt(index) == '"') || insidequotes) {
                if (input.charAt(index) == '"') {
                    if (!insidequotes) {
                        insidequotes = true;
                        if (index > lastletter)
                            row.add(input.substring(lastletter, index)
                                    .toLowerCase());
                        lastletter = index + 1;
                    } else {
                        insidequotes = false;
                        if ((index) > lastletter)
                            row.add(input.substring(lastletter, index)
                                    .toLowerCase());
                        lastletter = index + 1;
                    }
                }
            } else if (input.charAt(index) == ' ') {
                if (index > lastletter)
                    row.add(input.substring(lastletter, index).toLowerCase());
                lastletter = index + 1;
            } else if ((input.charAt(index) == '|')
                    || (input.charAt(index) == '&')) {
                if (index > lastletter)
                    row.add(input.substring(lastletter, index).toLowerCase());
                if (row.size() > 0)
                    data.add(row);
                lastletter = index + 1;
                row = new Vector();
            }
            index++;
        }
        if (index > lastletter)
            row.add(input.substring(lastletter, index).toLowerCase());
        data.add(row);

        // if (debugmode) {
        // System.out.println("SEARCH PARSE: " + data.size() + " rows");
        // for (int i = 0; i < data.size(); ++i) {
        // row = (Vector)data.get(i);
        // System.out.println("ROW " + String.valueOf(i) + ", Total entries: " +
        // String.valueOf(row.size()));
        // for (int j = 0; j < row.size(); ++j) {
        // String val = (String)row.get(j);
        // System.out.println("\"" + val + "\"");
        // }
        // }
        // }
    }

    public boolean getStatus(String str) {
        for (int i = 0; i < data.size(); ++i) {
            Vector rowdata = (Vector) data.get(i);
            boolean ismember = true;
            for (int j = 0; j < rowdata.size(); ++j)
                if (!StringUtil.substring((String) rowdata.get(j), str))
                    ismember = false;
            if (ismember)
                return true;
        }
        return false;
    }

    public boolean getRowStatus(String str, int row) {
        Vector rowdata = (Vector) data.get(row);
        boolean ismember = true;
        for (int j = 0; j < rowdata.size(); ++j)
            if (!StringUtil.substring((String) rowdata.get(j), str))
                ismember = false;
        if (ismember)
            return true;
        return false;
    }

    public boolean isEmpty() {
        for (int i = 0; i < data.size(); ++i) {
            Vector rowdata = (Vector) data.get(i);
            for (int j = 0; j < rowdata.size(); ++j)
                if (!((String) rowdata.get(j)).equals(""))
                    return false;
        }
        return true;
    }

    int num_rows;
    Vector data;
}
