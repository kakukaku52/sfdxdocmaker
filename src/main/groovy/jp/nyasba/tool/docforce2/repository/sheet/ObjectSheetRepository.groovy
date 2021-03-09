package jp.nyasba.tool.docforce2.repository.sheet

import jp.nyasba.tool.docforce2.domain.SfdcCustomObject
import jp.nyasba.tool.docforce2.domain.recordtype.SfdcRecordType
import jp.nyasba.tool.docforce2.repository.CellUtil
import jp.nyasba.tool.docforce2.repository.cellstyle.CellStyleUtil
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.PrintSetup
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook

/**
 * Excelの「オブジェクト情報」シートを作成するためのRepository
 */
class ObjectSheetRepository {

    def createSheet(Workbook workbook, SfdcCustomObject customObject){

        Sheet objectSheet = workbook.getSheet("オブジェクト情報")

        def normal = CellStyleUtil.normal(workbook)
        def inactive = CellStyleUtil.inactive(workbook)

        objectSheet.createRow(2)
        CellUtil.setValue(objectSheet, 2, 0, customObject.表示ラベル(), normal)
        CellUtil.setValue(objectSheet, 2, 1, customObject.API参照名(), normal)
        CellUtil.setValue(objectSheet, 2, 2, customObject.説明(), normal)
        
        List<SfdcRecordType> recordTypeList = customObject.レコードタイプリスト()
        recordTypeList.eachWithIndex {
            v, i ->
                if(v.isActive()){
                    writeRow(objectSheet, i+7, v, normal)
                }
                else {
                    writeRow(objectSheet, i+7, v, inactive)
                }
        }
    
        印刷設定(objectSheet)
    
    }
    
    private writeRow(Sheet sheet, int rowNumber, SfdcRecordType recordType, CellStyle style){
        println recordType.dump()
        sheet.createRow(rowNumber)
        CellUtil.setValue(sheet, rowNumber, 0, recordType.ラベル(), style)
        CellUtil.setValue(sheet, rowNumber, 1, recordType.API参照名(),  style)
        CellUtil.setValue(sheet, rowNumber, 2, recordType.説明(), style)
    }
    
    def void 印刷設定(Sheet sheet){
        PrintSetup printSetup = sheet.getPrintSetup()
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        printSetup.setLandscape(true);//横向き
        printSetup.setScale(80 as short)
    }
}
