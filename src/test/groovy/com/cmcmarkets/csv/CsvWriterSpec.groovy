package com.cmcmarkets.csv

import org.junit.Test
import com.cmcmarkets.beans.BeanType
import static com.cmcmarkets.Util.date
import static com.cmcmarkets.beans.Bean.beans

/**
 * User: dima
 * Date: 15/2/11
 */
class CsvWriterSpec {

  @Test public void shouldCreateEmptyCsvForEmptyInput() {
    def csv = new StringWriter()
    new CsvWriter().writeTo(csv, [])
    assert csv.toString() == ""
  }

  @Test public void shouldWriteCollectionOfBeansToCsv() {
    def csv = new StringWriter()
    new CsvWriter().writeTo(csv, beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0]))

    assert csv.toString() == """a,b,c
A,2,3.0
B,4,5.0
"""
  }

  @Test public void shouldWriteCollectionOfBeansToCsv_WithQuotes() {
    def csv = new StringWriter()
    new CsvWriter().withQuotes().writeTo(csv, beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0]))

    assert csv.toString() == """"a","b","c"
"A","2","3.0"
"B","4","5.0"
"""
  }

  @Test public void shouldWriteToCsvUnionOfAllBeansFields_UsingEmptyStringForNulls() {
    def csv = new StringWriter()
    new CsvWriter().writeTo(csv, beans([a: "A"], [b: 4], [c: 3.0]))

    assert csv.toString() == """a,b,c
A,,
,4,
,,3.0
"""
  }

  @Test public void shouldWriteBeanFieldsInSpecifiedOrder() {
    def csv = new StringWriter()
    def beans = beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0])
    new CsvWriter().usingFieldOrder(["c", "a"]).writeTo(csv, beans)

    assert csv.toString() == """c,a,b
3.0,A,2
5.0,B,4
"""
  }

  @Test public void shouldWriteBeansUsingSpecifiedHeader() {
    def csv = new StringWriter()
    def beans = beans([a: "A", b: 2, c: 3.0], [a: "B", b: 4, c: 5.0])
    new CsvWriter().withHeader(["c", "d", "a"]).writeTo(csv, beans) // "d" column doesn't exist but should be used anyway

    assert csv.toString() == """c,d,a
3.0,,A
5.0,,B
"""
  }

  @Test public void shouldUseConvertors() {
    def csv = new StringWriter()
    def beans = beans([a: date(17, 02, 2011), b: 2, c: 3.0], [a: date(20, 02, 2011), b: 4, c: 5.0])
    new CsvWriter().usingConvertors(["a" : BeanType.DATE_AS_STRING("dd/MM/yyyy")])
            .writeTo(csv, beans)

    assert csv.toString() == """a,b,c
17/02/2011,2,3.0
20/02/2011,4,5.0
"""
  }

  @Test public void shouldEscapeQuoteAndCommas() {
    def csv = new StringWriter()
    def beans = beans(
            [a: "1", b: "2,2", c: "3"],
            [a: "1,1", b: "2", c: "3"],
            [a: "1", b: "2,2", c: "3,3"],
            [a: "1", b: "\"2\"", c: "\"3\"3"]
    )
    new CsvWriter().writeTo(csv, beans)

    assert csv.toString() == """a,b,c
1,"2,2",3
"1,1",2,3
1,"2,2","3,3"
1,""2"",""3""3
"""
  }
}
