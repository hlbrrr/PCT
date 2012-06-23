package com.compassplus.gui;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: hlbrrr
 * Date: 26.03.12
 * Time: 22:10
 */
public class RowStyle {
    private HashMap<Integer, CellStyle> styles = new HashMap<Integer, CellStyle>();
    private Integer last = -1;
    private Integer first = 99999;
    public RowStyle() {
    }

    public void init(Workbook wb, Row row) {
        styles.clear();
        if (row != null) {
            int from = row.getFirstCellNum();
            int to = row.getLastCellNum();
            last = new Integer(to);
            first = new Integer(from);
            for (; from <= to; from++) {
                try {
                    CellStyle cs = wb.createCellStyle();
                    CellStyle d = row.getCell(from).getCellStyle();
                    cs.cloneStyleFrom(d);
                    styles.put(from, cs);
                } catch (Exception e) {

                }
            }
        }

    }

    public Integer getFirst(){
        return first;
    }

    public Integer getLast(){
        return last;
    }

    public CellStyle getCellStyle(Integer index, CellStyle def) {
        return styles.containsKey(index) ? styles.get(index) : def;
    }
}
