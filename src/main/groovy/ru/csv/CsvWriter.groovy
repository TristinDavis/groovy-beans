package ru.csv

import ru.beans.Bean

 /**
 * Reads {@link ru.beans.Bean}s from csv file.
 *
 * Should:
 *  + write to file collection of beans
 *  - write to file bean after bean
 *
 *  + write beans as is to .csv file
 *    - active writer: get csv header as a union of all possible fields in beans list
 *    - passive writer: get csv header as fields from the first bean
 *    -- (probably it should be done before writing using operations on beans) instructed writer: writes specified subset of fields
 *  + write bean fields in particular order
 *  + write beans using specific convertors (e.g. date convertors)
 *
 *  - detect when beans don't match header
 *    - nice writer: adds empty string
 *    - strict writer: fail fast
 *
 *  - append to existing .csv file (i.e. read file header and write beans accordingly)
 *
 * User: dima
 * Date: 15/2/11
 */
class CsvWriter {
  private def header = []
  private def fieldsOrder = []
  private Map convertors = [:]

  CsvWriter usingFieldOrder(List<String> fieldsOrder) {
    this.fieldsOrder = fieldsOrder
    this
  }

  CsvWriter usingConvertors(Map convertors) {
    this.convertors = convertors
    this
  }

  def writeTo(String fileName, List<Bean> beans) {
    writeTo(new FileWriter(fileName), beans)
  }

  def writeTo(Writer writer, List<Bean> beans) {
    if (beans.empty) return
    header = getHeaderFrom(beans)

    writer.withWriter { w ->
      w.append(headerAsString(header))
      beans.each { w.append(beanAsString(it)) }
    }
  }

  private String beanAsString(def bean) {
    header.collect {
      def value = bean."$it"
      if (convertors.containsKey(it)) {
        value = convertors.get(it).convert(value)
      }
      asString(value)
    }.join(",") + "\n"
  }

  private def asString(value) {
    if (value == null) return "" // I don't think anyone ever need "null" values in .csv file
    value = value.toString()
    value = value.replaceAll("\"", "\"\"")
    if (value.contains(",")) value = "\"$value\""
    value
  }

  private String headerAsString(List header) {
    return header.join(",") + "\n"
  }

  private List getHeaderFrom(List beans) {
    def header = new LinkedHashSet()
    beans.each { bean ->
      bean.fieldNames().each { header.add(it) }
    }.toList()

    fieldsOrder + (header - fieldsOrder)
  }
}
