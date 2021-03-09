package jp.nyasba.tool.docforce2.domain.field

import groovy.util.slurpersupport.GPathResult
import jp.nyasba.tool.docforce2.domain.condition.SfdcOperation

/**
 * 参照関係フィールド
 */
class SfdcLookupField implements SfdcField {

    def fieldXml

    def SfdcLookupField(GPathResult fieldXml){
        this.fieldXml = fieldXml
    }

    @Override
    def String ラベル(){
        return fieldXml.label
    }

    @Override
    String API参照名() {
        return fieldXml.fullName
    }

    @Override
    String タイプ() {
        return "${fieldXml.type}(${fieldXml.referenceTo})"
    }

    @Override
    String length() {
        return "-"
    }

    @Override
    String デフォルト値or選択リスト値() {
        return filter()
    }

    @Override
    String 数式() {
        return "" // 数式は設定できない
    }

    @Override
    String ヘルプテキスト() {
        return fieldXml.inlineHelpText
    }

    @Override
    String 必須() {
        return fieldXml.required == "true" ? "○" : ""
    }

    @Override
    String 外部ID() {
        return "" // 外部IDにはできない
    }

    @Override
    String 説明() {
        return fieldXml.description
    }
    
    def String filter(){
        if(fieldXml.lookupFilter == null || fieldXml.lookupFilter.active != "true"){
            return ""
        }
        def filterItemMsg = fieldXml.lookupFilter.filterItems.collect { "${it.field} ${SfdcOperation.convert(it.operation)} ${it.value}${it.valueField}" }.join("\n")
        return "[filter]\n" + filterItemMsg
    }
    
}
