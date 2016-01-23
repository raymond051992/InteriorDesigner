package com.ggwp.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.RoomDesignData;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Dell on 1/22/2016.
 */
public class ToolUtils {

    public static <T> T getParamValue(Object source,Class<T> cls,String paramName){
        if(source!=null&&source instanceof Map){
            Map map = (Map)source;
            if(map.containsKey(paramName)) {
                if (cls.isInstance(map.get(paramName))) return cls.cast(map.get(paramName));
            }
        }
        return null;
    }

    public static  Map<String,Object> createMapFromList(Object[][] arrays){
        HashMap<String,Object> obj = new HashMap<String, Object>();

        for (Object[] array:arrays
             ) {
            obj.put((String)array[0],array[1]);
        }

        return obj;

    }

    public static FileHandle findFileByAbsolutePath(String absolutePath){

          Object[][] tests = {{"title", "test error"},
                {"message", absolutePath}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
        FileHandle tmplate = Gdx.files.absolute(absolutePath);
        /*File[] files = new File[tmplates.length];
        int i = 0;
        for (FileHandle fh:tmplates) {
            System.out.println(fh.file().getName());
            files[i++] = fh.file();
        }*/

        if(tmplate==null)
            return null;

        return tmplate;
    }

    public static FileHandle fetchLatestSnapshot(){
        System.out.println(Main.screenTemplateSaveDirectory);

        FileHandle[] tmplates = Gdx.files.absolute(Main.screenTemplateSaveDirectory).list();
        /*File[] files = new File[tmplates.length];
        int i = 0;
        for (FileHandle fh:tmplates) {
            System.out.println(fh.file().getName());
            files[i++] = fh.file();
        }*/

        if(tmplates==null)
            return null;

        if(tmplates.length==0){
            return null;
        }
        Arrays.sort(tmplates, new Comparator<FileHandle>() {
            public int compare(FileHandle f1, FileHandle f2) {
                // sort latest first
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        for (FileHandle fhx:tmplates) {
            System.out.println(fhx.file().getName());
        }
        return tmplates[0];
    }

    private static final String SAVED_ROOM_DESIGN_DIRECTORY = "/savedrooms/";

    public static void saveRoomDataDesign(RoomDesignData data){
        try {
            System.out.println("Saving room data design..");

            FileHandle directory = Gdx.files.external(SAVED_ROOM_DESIGN_DIRECTORY);
            createDirectoryIfNotExists(directory);

            String nextFileName = getRoomNextFileName(directory);
            System.out.println(nextFileName);

            FileHandle newFile = Gdx.files.external(SAVED_ROOM_DESIGN_DIRECTORY + nextFileName);

            System.out.println("Saving file..");
            Gson gson = new Gson();
            String json = gson.toJson(data);
            newFile.writeString(json, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRoomNextFileName(FileHandle directory){
        System.out.println("Getting room next file name..");
        Integer max = 0;

        for(FileHandle fileHandle : directory.list()){
            try {
                Integer id = Integer.parseInt(fileHandle.nameWithoutExtension());

                if(id > max){
                    max = id;
                }
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
        }
        return String.valueOf(max + 1) + ".json";
    }

    private static void createDirectoryIfNotExists(FileHandle directory) throws IOException {
        System.out.println("Checking directory..");
        if(!directory.exists()){
            System.out.println("Creating directory..");
            directory.file().mkdirs();
        }
    }

    public static  void initInputProcessors(Stage stage){
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.getProcessors().clear();
        im.addProcessor((AppScreen)Main.getInstance().getScreen());
        im.addProcessor(stage);
    }

    public static void removeScreenInputProcessor(Stage stage){
        InputMultiplexer im;
        Object o = Gdx.input.getInputProcessor();
        if(o instanceof  InputMultiplexer){
            im= (InputMultiplexer)o;
        }
        else{
            im= new InputMultiplexer(stage,(AppScreen)Main.getInstance().getScreen());
            Gdx.input.setInputProcessor(im);
        }

        im.getProcessors().clear();
        im.addProcessor(stage);
    }

}
