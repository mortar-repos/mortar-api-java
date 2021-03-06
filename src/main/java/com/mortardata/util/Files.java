/*
 * Copyright 2013 Mortar Data Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mortardata.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * File manipulation utilities.
 */
public class Files {
    
    /**
     * Create a temporary directory.
     * 
     * @return new temporary directory
     * @throws IOException if unable to make temporary directory
     */
    public static File createTempDirectory() throws IOException {
        // create a file
        File temp = File.createTempFile("temp", null);
        
        // delete it
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        
        // remake a directory in its place
        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return temp;
    }
    
    /**
     * Is the provided directory empty?
     * 
     * @param directory Directory to check
     * @return whether directory is empty
     * @throws IOException if directory does not exist
     */
    public static boolean isEmpty(File directory) throws IOException {
        String[] contents = directory.list();
        if (contents == null) {
            throw new IOException("Directory " + directory + " not found");
        }
        return contents.length == 0;
    }
    
    /**
     * Create a zero-byte file at provided path if one doesn't
     * already exist.
     * 
     * @param file Location for file to be created
     * @throws IOException if file cannot be created
     */
    public static void touch(File file) throws IOException {
        new FileOutputStream(file).close();
    }
    
}
