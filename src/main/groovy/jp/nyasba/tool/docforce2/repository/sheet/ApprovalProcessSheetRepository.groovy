package jp.nyasba.tool.docforce2.repository.sheet

import jp.nyasba.tool.docforce2.domain.approvalprocess.SfdcApprovalProcess
import jp.nyasba.tool.docforce2.domain.approvalprocess.SfdcApprovalProcessAction
import jp.nyasba.tool.docforce2.domain.approvalprocess.SfdcApprovalProcessStep
import jp.nyasba.tool.docforce2.repository.CellUtil
import jp.nyasba.tool.docforce2.repository.cellstyle.CellStyleUtil
import jp.nyasba.tool.docforce2.repository.cellstyle.RowHeightUtil
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress

/**
 * Excelの「承認プロセス」シートを作成するためのRepository
 */
class ApprovalProcessSheetRepository {
    
    CellStyle sectionTitle;
    CellStyle normal;
    CellStyle normalWithTopBold;
    CellStyle tableHeader;
    CellStyle tableHeader2;

    def createSheets(Workbook workbook, List<SfdcApprovalProcess> approvalProcessList){
        
        // 利用するスタイルを作成
        sectionTitle = CellStyleUtil.sectionTitle(workbook)
        normal = CellStyleUtil.normal(workbook)
        normalWithTopBold = CellStyleUtil.normalWithTopBold(workbook)
        tableHeader = CellStyleUtil.tableHeader(workbook)
        tableHeader2 = CellStyleUtil.tableHeader2(workbook)
    
        approvalProcessList.eachWithIndex{ SfdcApprovalProcess ap, int i -> createSheet(workbook, ap, i) }
        workbook.removeSheetAt(workbook.getSheetIndex("承認プロセス"))
    }
    
    def createSheet(Workbook workbook, SfdcApprovalProcess ap, int i){
        Sheet sheet = workbook.cloneSheet(workbook.getSheetIndex("承認プロセス"))
        workbook.setSheetName(workbook.getSheetIndex(sheet), "承認プロセス(${i+1})")
        workbook.setSheetOrder("承認プロセス(${i+1})", i+5)
    
        int row = 1;

        承認プロセス情報1行(sheet, row++, "表示ラベル", ap.表示ラベル())
        承認プロセス情報1行(sheet, row++, "API参照名", ap.API参照名())
        承認プロセス情報1行(sheet, row++, "説明", ap.説明())
        承認プロセス情報1行(sheet, row++, "開始条件", ap.開始条件())
        承認プロセス情報1行(sheet, row++, "レコードの編集", ap.レコードの編集())
        承認プロセス情報1行(sheet, row++, "申請者の取り消し", ap.申請者の取り消し())
        承認プロセス情報1行(sheet, row++, "承認割り当てメールテンプレート", ap.承認割り当てメールテンプレート())
        承認プロセス情報1行(sheet, row++, "承認ページ表示項目", ap.承認ページ表示項目())
    
        row++
        CellUtil.setValueWithCreateRecord(sheet, row++, 0, "申請・取消時", sectionTitle, 24 as float)
        row = アクションリスト(sheet, row, "申請時のアクション", ap.申請時のアクションリスト())
        row = アクションリスト(sheet, row, "取消時のアクション", ap.取消時のアクションリスト())
        
        row++
        CellUtil.setValueWithCreateRecord(sheet, row++, 0, "承認ステップ", sectionTitle, 24 as float)
        row = 承認ステップ(sheet, row, ap.承認ステップリスト())
    
        row++
        CellUtil.setValueWithCreateRecord(sheet, row++, 0, "最終承認・却下時", sectionTitle, 24 as float)
        row = アクションリスト(sheet, row, "最終承認時のアクション", ap.最終承認時のアクションリスト())
        row = アクションリスト(sheet, row, "最終却下時のアクション", ap.最終却下時のアクションリスト())

        印刷設定(sheet)
    
    }
    
    def void 承認プロセス情報1行(Sheet sheet, int row, String key, String value){
        Row r =sheet.createRow(row)
        CellUtil.setValueAndCellsMerged(sheet, row, 0, 1, key, tableHeader)
        CellUtil.setValueAndCellsMerged(sheet, row, 2, 3, value, normal)
        r.setHeightInPoints(RowHeightUtil.optimizedValue(value))
    }
    
    def int アクションリスト(Sheet sheet, int row, String actionLabel, List<SfdcApprovalProcessAction> actionList, int colposition = 1, int merged = 2){

        if(actionList.size() == 0){
            return row
        }

        int originalRow = row
        CellUtil.setValueWithCreateRecord(sheet, row, colposition, actionLabel, tableHeader2)
        CellUtil.setValue(sheet, row, colposition+1, "種別", tableHeader2)
        CellUtil.setValueAndCellsMerged(sheet, row, colposition+2, colposition+2+merged-1, "名前", tableHeader2)
        row++
        actionList.each {
            CellUtil.setValueWithCreateRecord(sheet, row, colposition, "", tableHeader2)
            CellUtil.setValue(sheet, row, colposition+1, it.type, normal)
            CellUtil.setValueAndCellsMerged(sheet, row, colposition+2, colposition+2+merged-1, it.name, normal)
            row++
        }
        sheet.addMergedRegion(new CellRangeAddress(originalRow, row -1 , colposition, colposition))
        return row
    }
    
    def int 承認ステップ(Sheet sheet, int row, List<SfdcApprovalProcessStep> stepList){
        sheet.createRow(row)
        CellUtil.setValueAndCellsMerged(sheet, row, 0, 1, "ラベル", tableHeader)
        CellUtil.setValue(sheet, row, 2, "API参照名", tableHeader)
        CellUtil.setValue(sheet, row, 3, "条件", tableHeader)
        CellUtil.setValue(sheet, row, 4, "承認割り当て先", tableHeader)
        CellUtil.setValue(sheet, row, 5, "代理承認", tableHeader)
        CellUtil.setValue(sheet, row, 6, "却下時の処理", tableHeader)
        CellUtil.setValue(sheet, row, 7, "説明", tableHeader)
        row++
        stepList.each {
            def originalRow = row
            Row r = sheet.createRow(row)
            CellUtil.setValue(sheet, row, 0, it.ラベル, normalWithTopBold)
            CellUtil.setStyle(sheet, row, 1, normalWithTopBold)
            CellUtil.setValue(sheet, row, 2, it.API参照名, normalWithTopBold)
            CellUtil.setValue(sheet, row, 3, it.条件, normalWithTopBold)
            CellUtil.setValue(sheet, row, 4, it.承認割り当て先, normalWithTopBold)
            CellUtil.setValue(sheet, row, 5, it.代理承認, normalWithTopBold)
            CellUtil.setValue(sheet, row, 6, it.却下時の処理, normalWithTopBold)
            CellUtil.setValue(sheet, row, 7, it.説明, normalWithTopBold)
            r.setHeightInPoints( [
                    RowHeightUtil.optimizedValue(it.条件),
                    RowHeightUtil.optimizedValue(it.承認割り当て先),
                    RowHeightUtil.optimizedValue(it.却下時の処理),
                    RowHeightUtil.optimizedValue(it.説明 as String),
            ].max())
            row++
            row = アクションリスト(sheet, row, "承認時のアクション", it.承認時のアクションリスト,2,4)
            row = アクションリスト(sheet, row, "却下時のアクション", it.却下時のアクションリスト,2,4)
            
            if(originalRow < row-1 ) {
                // アクションリスト行のマージ対象セルにセルスタイルを適用。
                (originalRow + 1 .. row - 1).each {
                    CellUtil.setStyle(sheet, it, 0, normal)
                    CellUtil.setStyle(sheet, it, 1, normal)
                }
                sheet.addMergedRegion(new CellRangeAddress(originalRow, row - 1, 0, 1))
            }
        }
        return row
    }
    
    
    def void 印刷設定(Sheet sheet){
        PrintSetup printSetup = sheet.getPrintSetup()
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        printSetup.setLandscape(true);//横向き
        printSetup.setScale(50 as short)
    }
}
