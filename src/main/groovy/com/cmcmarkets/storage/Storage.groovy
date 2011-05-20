package com.cmcmarkets.storage

import com.thoughtworks.xstream.XStream
import com.thoughtworks.xstream.io.xml.StaxDriver

/**
 * User: dima
 * Date: 19/3/11
 */
class Storage { // TODO document
  private static final String STORAGE = ".storage"
  static def xStream = new XStream(new StaxDriver())

  static def assertSameAsLast(String id, String storage = STORAGE, Closure closure) {
    def lastValue = load(id, storage)
    if (lastValue == null) {
      lastValue = closure()
      save(id, lastValue, storage)
    }
    assert lastValue == closure()
  }

  static def cachedReload(String id, Closure closure) {
    def result = closure.call()
    save(id, result)
    result
  }

  static def cached(String id, Closure closure) {
    def result = load(id)
    if (result == null) {
      result = closure.call()
      if (result != null) save(id, result)
    }
    result
  }

  static def load(String id, String storage = STORAGE) {
    def result = loadAsCollection(id, storage)
    if (result == null || result.empty) return null
    result[0]
  }

  private static def loadAsCollection(String id, String storage = STORAGE) {
    def file = fileFor(id, storage)
    if (!file.exists()) return null

    def result = []
    def inputStream = xStream.createObjectInputStream(new BufferedReader(new FileReader(file)))
    inputStream.withStream { stream ->
      try {
        //noinspection GroovyInfiniteLoopStatement
        while (true) result << stream.readObject()
      } catch (EOFException ignore) { // TODO this is ugly
      }
    }
    result
  }

  static def save(String id, def object, String storage = STORAGE) {
    saveAsCollection(id, [object], storage)
  }

  private static def saveAsCollection(String id, Collection collection, String storage = STORAGE) {
    createStorageFolder(storage)
    def file = fileFor(id, storage)
    file.createNewFile()

    // the main reason to use Stream instead of just String is that in case of saving Collection,
    // String requires about the same amount of memory as Collection itself
    ObjectOutputStream outputStream = xStream.createObjectOutputStream(new BufferedWriter(new FileWriter(file)))
    outputStream.withStream { stream ->
      collection.each { stream.writeObject(it) }
    }
  }

  static boolean delete(String id, String storage = STORAGE) {
    def file = fileFor(id, storage)
    if (!file.exists()) return false
    file.delete()
    true
  }

  private static def createStorageFolder(String storage) {
    def storageFolder = new File(storage)
    if (!storageFolder.exists()) storageFolder.mkdir()
  }

  private static File fileFor(String id, String storage) {
    new File("${storage}/${id}.xml")
  }
}