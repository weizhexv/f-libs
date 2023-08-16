package com.jkqj.xlsx.output;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import com.aspose.cells.WorkbookDesigner;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class TemplatedXlsxOutput implements XlsxOutput {
    private final String template;
    private final String dataObjName;

    {
        CellInitializer.init();
    }

    /**
     *
     * @param template excel 模板名称, 可以放到resource下面
     * @param dataObjName 模板中变量的名称， 比如示例tpl.xlsx中的"Order"
     */
    public TemplatedXlsxOutput(String template, String dataObjName) {
        this.template = template;
        this.dataObjName = dataObjName;
    }

    @Override
    public <T> void output(List<T> dataList, OutputStream outputStream) {
        if (dataList == null || dataList.size() == 0) {
            log.info("empty list,tpl is :{}", template);
            return;
        }
        try (InputStream inputStream = TemplatedXlsxOutput.class.getClassLoader().getResourceAsStream(template)) {
            Workbook workbook = new Workbook(inputStream);
            WorkbookDesigner designer = new WorkbookDesigner();
            designer.setWorkbook(workbook);
            designer.setDataSource(dataObjName, dataList);
            designer.process(false);
            workbook.save(outputStream, SaveFormat.XLSX);
        } catch (IOException e) {
            log.error("output excel error,{},first data:{}", template, dataList.get(0), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("output excel error,{},first data:{}", template, dataList.get(0), e);
            throw new RuntimeException(e);
        }
    }
}
