package org.translateToSql.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.translateToSql.model.InputFileData;
import org.translateToSql.utils.FileUtils;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        // run an API
        if (args.length == 0) SpringApplication.run(Main.class, args);

        // translate a file
        else{
            InputFileData fileData = FileUtils.parseFile(args[0]);
            TranslateFromTwoVL algo = new TranslateFromTwoVL(fileData.getSchema());
            FileUtils.translateFile(fileData, algo);
        }
    }
}

