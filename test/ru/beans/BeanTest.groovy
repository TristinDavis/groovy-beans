package ru.beans

import org.junit.Test
import ru.beans.BeanType
import ru.beans.Bean

/**
 * User: dima
 * Date: 8/2/11
 */
class BeanTest {
  @Test public void shouldStoreValuesAssignedToNotExistingProperties() {
    def bean = new Bean()
    bean.a = 123
    bean.b = "abc"

    assert bean.a == 123
    assert bean.b == "abc"
  }

  @Test public void shouldBeAbleToSetPropertyWithTheSameNameAsInternalMap() {
    def bean = new Bean()
    bean.data = 123
    assert bean.data == 123
  }

  @Test public void equalityShouldWorkForDynamicProperties() {
    def bean1 = new Bean()
    def bean2 = new Bean()
    assert [:] == [:]
    assert bean1.equals(bean2)

    bean1.a = 123
    assert bean1 != bean2

    bean2.a = 123
    assert bean1 == bean2
    assert bean1.a == bean2.a
  }

  @Test public void shouldHaveToStringMethod() {
    def bean = new Bean()
    bean.a = 1.0

    assert bean.toString() == "[a:1.0]"
  }

  @Test public void shouldUseTypeWhenPropertyIsAssigned() {
    def bean = new Bean().withType([name: BeanType.STRING, id: BeanType.INTEGER, price: BeanType.DOUBLE])

    bean.name = "aName"
    assert bean.name == "aName"
    bean.name = 123
    assert bean.name == "123"

    bean.id = 123
    assert bean.id == 123
    bean.id = "234"
    assert bean.id == 234

    bean.price = 1.2d
    assert bean.price == 1.2
    bean.price = "1.23"
    assert bean.price == 1.23
  }

  @Test public void shouldUseTypeWhenBeanIsCreatedFromMap() {
    def data = [name:"aName", id: "123", price: "1.23"]
    def bean = new Bean(data, [name: BeanType.STRING, id: BeanType.INTEGER, price: BeanType.DOUBLE])

    assert bean.name == "aName"
    assert bean.id == 123
    assert bean.price == 1.23
  }
}