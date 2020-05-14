/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zipfiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.Arrays;

/**
 *
 * @author kenji
 */
public class ZipFiles {

    private ArrayList<String> fileList;
    private static String path = "", nameFolder = "";
    private static String folderZipFinal = "";
    private static ZipFiles listFiles = new ZipFiles();

    public ZipFiles() {
        fileList = new ArrayList<>();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Passar o caminho inteiro do path
        if(args.length == 0){
            System.out.println("Informe um diretório a ser compactado:");
            Scanner reader = new Scanner(System.in);
            path = reader.nextLine();
        }else 
            path  = args[0];
        
         
        File namePath = new File(path);
//        listFiles.generateFileList(namePath.listFiles());
        listFiles.listFilesForFolder(namePath);
    }

    public void zipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        String source = new File(path).getPath();
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(source + nameFolder + File.separator + zipFile + ".zip");
            zos = new ZipOutputStream(fos);
            FileInputStream in = null;
            System.out.println(" * " + fileList);
            for (String file : this.fileList) {
                System.out.println("Arquivos adicionados: " + file);
                ZipEntry ze = new ZipEntry(file);
                zos.putNextEntry(ze);
                try {                    
                    in = new FileInputStream(path + nameFolder + File.separator + file);                    
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    in.close();
                }
            }
            zos.closeEntry();
            System.out.println("Zip gerado com sucesso!");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void listFilesForFolder(final File folder) {
//        Laço de leitura do path e indentificação de folder ou file
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
//                Pegar o nome do folder em que estão os arq
                nameFolder = fileEntry.toString().substring(fileEntry.toString().lastIndexOf(File.separator));                
                listFiles.generateFileList(fileEntry.listFiles());
            }
//            else {
//                        System.out.println("2: "+ fileEntry);
//                listFiles.generateFileList(fileEntry.listFiles());
//            }
        }
    }

    //método captura o arq, insere na lista e manda gerar um zip
    public void generateFileList(File[] node) {

        ArrayList<String> onlyName = new ArrayList<>();
        ArrayList<String> pathFull = new ArrayList<>();
        String nameLast = "";
//        Captura somente o nome dos arq
        for (int i = 0; i < node.length; i++) {
            if (node[i].isFile()) {                
                onlyName.add(node[i].toString().substring(node[i].toString().lastIndexOf(File.separator) + 1, node[i].toString().lastIndexOf('.')));
                pathFull.add(node[i].toString());
            }
        }        
        while (!pathFull.isEmpty()) {
            //nome do primeiro element pro nameFirst pra comparar e folderZipFinal pro nome do zip
            if (pathFull.size() == 1) {
                pathFull.remove(pathFull.get(0));
                onlyName.remove(onlyName.get(0));
                return;
            }

            int index = onlyName.size() - 1;
//            pega o nome do ultimo arq
            folderZipFinal = nameLast = onlyName.get(index);
//            Limpa a lista que armazena os arq pra zip
            fileList.clear();
//          Insere o ultimo arq na lista pro zip
            fileList.add(generateZipEntry(pathFull.get(index)));
//          Remove o ultimo item da lista, esse item é o nameLast
            pathFull.remove(pathFull.get(index));
            onlyName.remove(index);
            if (onlyName.size() > 0) {
                for (int i = index - 1; i >= 0; i--) {
//                compara os arq que tem nomes iguais
                    if (nameLast.equals(onlyName.get(i))) {
                        fileList.add(generateZipEntry(pathFull.get(i)));
//                      Remove o atual item da lista que é igual ao nameLast
                        pathFull.remove(pathFull.get(i));
                        onlyName.remove(i);
                    }
                }
            }
            System.out.println(fileList);
            if (fileList.size() > 1) {
                listFiles.zipIt(folderZipFinal);
            }
        }
    }

    //Pega o path e retorna somente o nome do arq com a extensão
    private String generateZipEntry(String file) {
        return file.substring(file.lastIndexOf(File.separator) + 1, file.length());
    }
}
