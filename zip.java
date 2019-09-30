import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

class ZipFile {
    public static void main(String[] args) throws IOException {
        String sourceFile = "test1.txt";
        FileOutputStream fileOutputStream = new FileOutputStream("compressed.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        File file = new File(sourceFile);
        FileInputStream fileInputStream = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOutputStream.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fileInputStream.read(bytes)) >= 0) {
            zipOutputStream.write(bytes, 0, length);
        }
        zipOutputStream.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
class ZipMultipleFiles {
    public static void main(String[] args) throws IOException {
        List<String> srcFiles = Arrays.asList("test1.txt", "test2.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("multiCompressed.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        for (String srcFile : srcFiles) {
            File file = new File(srcFile);
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while((length = fileInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }
            fileInputStream.close();
        }
        zipOutputStream.close();
        fileOutputStream.close();
    }
}

class ZipDirectory {
    public static void main(String[] args) throws IOException {
        String sourceFile = "zipTest";
        FileOutputStream fileOutputStream = new FileOutputStream("dirCompressed.zip");
        ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
        File file = new File(sourceFile);
        zipFile(file, file.getName(), zipOutputStream);
        zipOutputStream.close();
        fileOutputStream.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            assert children != null;
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fileInputStream.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fileInputStream.close();
    }
    public static class UnzipFile {
        public static void main(String[] args) throws IOException {
            String fileZip = "src/main/resources/unzipTest/compressed.zip";
            File destDir = new File("src/main/resources/unzipTest");
            byte[] buffer = new byte[1024];
            ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                int len;
                while ((len = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, len);
                }
                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
        }

        static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
            File destFile = new File(destinationDir, zipEntry.getName());

            String destDirPath = destinationDir.getCanonicalPath();
            String destFilePath = destFile.getCanonicalPath();

            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
            }

            return destFile;
        }
    }

}
