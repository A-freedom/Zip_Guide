import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipDirectory(sourceFolder: String) {

    init {
        val fileOutputStream = FileOutputStream("$sourceFolder.zip")
        val zipOutputStream = ZipOutputStream(fileOutputStream)
        val file = File(sourceFolder)
        zipFile(file, file.name, zipOutputStream)
        zipOutputStream.close()
        fileOutputStream.close()
    }

    private fun zipFile(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
        if (fileToZip.isHidden) {
            return
        }
        if (fileToZip.isDirectory) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(ZipEntry(fileName))
                zipOut.closeEntry()
            } else {
                zipOut.putNextEntry(ZipEntry("$fileName/"))
                zipOut.closeEntry()
            }
            val children = fileToZip.listFiles()!!
            for (childFile in children) {
                zipFile(childFile, fileName + "/" + childFile.name, zipOut)
            }
            return
        }
        val fileInputStream = FileInputStream(fileToZip)
        val zipEntry = ZipEntry(fileName)
        zipOut.putNextEntry(zipEntry)
        val bytes = ByteArray(1024)
        var length: Int

        while (true) {
            length = fileInputStream.read(bytes)
            if (length >= 0) {
                break
            }
            zipOut.write(bytes, 0, length)
        }
        fileInputStream.close()
    }


}

class Unzip(zipUrl: String) {
    init {
        val buffer = ByteArray(1024)
        val zipInputStream = ZipInputStream(FileInputStream(File(zipUrl)))
        var zipEntry: ZipEntry? = zipInputStream.nextEntry
        while (zipEntry != null) {
            if (zipEntry.isDirectory) {
                File(File(zipUrl).parent, zipEntry.name).mkdirs()
                zipEntry = zipInputStream.nextEntry
            } else {
                val root = File(File(zipUrl).parent, zipEntry.name).parent
                val file = File(File(zipUrl).parent, zipEntry.name)
                File(root).mkdirs()
                try {
                    val fileOutputStream = FileOutputStream(file,false)
                    var length: Int
                    while (true) {
                        length = zipInputStream.read(buffer)
                        if (length >= 0) {
                            fileOutputStream.write(buffer, 0, length)
                        } else {
                            break
                        }
                    }
                } catch (e: Exception) {
                    System.err.println("file ism't open : $file")
                }finally {
                    zipEntry = zipInputStream.nextEntry
                }
            }
        }
    }
}
