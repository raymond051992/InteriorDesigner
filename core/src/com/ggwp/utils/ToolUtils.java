package com.ggwp.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.ggwp.interfaces.AndroidOnlyInterface;
import com.ggwp.interiordesigner.Main;
import com.ggwp.interiordesigner.object.AppScreen;
import com.ggwp.interiordesigner.object.GameObject;
import com.ggwp.interiordesigner.object.RoomDesignData;
import com.ggwp.interiordesigner.object.SaveFile;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolUtils {

    public static <T> T getParamValue(Object source, Class<T> cls, String paramName) {
        if (source != null && source instanceof Map) {
            Map map = (Map) source;
            if (map.containsKey(paramName)) {
                if (cls.isInstance(map.get(paramName))) return cls.cast(map.get(paramName));
            }
        }
        return null;
    }

    public static Map<String, Object> createMapFromList(Object[][] arrays) {
        HashMap<String, Object> obj = new HashMap<String, Object>();

        for (Object[] array : arrays
                ) {
            obj.put((String) array[0], array[1]);
        }

        return obj;

    }

    public static FileHandle findFileByAbsolutePath(String absolutePath) {

        Object[][] tests = {{"title", "test error"},
                {"message", absolutePath}};
        Main.aoi.requestOnDevice(AndroidOnlyInterface.RequestType.LOG,
                ToolUtils.createMapFromList(tests));
        FileHandle template = Gdx.files.absolute(absolutePath);

        if (template == null)
            return null;

        return template;
    }

    public static FileHandle fetchLatestSnapshot() {
        FileHandle[] templates = Gdx.files.absolute(Main.screenTemplateSaveDirectory).list();

        if (templates == null)
            return null;

        if (templates.length == 0) {
            return null;
        }
        Arrays.sort(templates, new Comparator<FileHandle>() {
            public int compare(FileHandle f1, FileHandle f2) {
                return Long.compare(f2.lastModified(), f1.lastModified());
            }
        });

        for (FileHandle fhx : templates) {
            System.out.println(fhx.file().getName());
        }
        return templates[0];
    }

    private static final String SAVED_ROOM_DESIGN_DIRECTORY = "/savedrooms/";
    private static final String SAVED_FILE_DIRECTORY = "/rooms/";
    private static final String EMPTY_ROOM_DESIGN_DIRECTORY = "/Rooms/Json/";

    public static void saveEmptyRoomDataDesign(RoomDesignData data) {
        try {
            FileHandle directory = Gdx.files.internal(EMPTY_ROOM_DESIGN_DIRECTORY);
            createDirectoryIfNotExists(directory);

            String nextFileName = getRoomNextFileName(directory);

            FileHandle newFile = Gdx.files.internal(EMPTY_ROOM_DESIGN_DIRECTORY + nextFileName);

            Gson gson = new Gson();
            String json = gson.toJson(data);
            newFile.writeString(json, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSaveFileDirAbsolutePath() {
        return Main.aoi.getProjectDirectory() + SAVED_FILE_DIRECTORY;
    }

    public static void saveRoomSetup(String name, GameObject[] gameObjs, RoomDesignData roomDesignData, Collection<SaveFile.TilePaint> tilePaints) {
        try {
            FileHandle directory = Gdx.files.external(Main.aoi.getProjectDirectory() + SAVED_FILE_DIRECTORY);
            createDirectoryIfNotExists(directory);

            FileHandle newFile = Gdx.files.absolute(directory + "/" + name);
            System.out.println("Saving file..");
            Gson gson = new Gson();

            SaveFile saveFile = new SaveFile();
            saveFile.roomDesignData = roomDesignData;
            saveFile.paintTiles = new ArrayList(tilePaints);

            for (GameObject obj : gameObjs) {
                saveFile.addObject(obj);
            }

            String json = gson.toJson(saveFile);
            newFile.writeString(json, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getRoomNextFileName(FileHandle directory) {
        Integer max = 0;

        for (FileHandle fileHandle : directory.list()) {
            try {
                Integer id = Integer.parseInt(fileHandle.nameWithoutExtension());

                if (id > max) {
                    max = id;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(max + 1) + ".json";
    }

    private static void createDirectoryIfNotExists(FileHandle directory) throws IOException {
        if (!directory.exists()) {
            directory.file().mkdirs();
        }
    }

    public static void initInputProcessors(Stage stage) {
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.getProcessors().clear();
        im.addProcessor((AppScreen) Main.getInstance().getScreen());
        im.addProcessor(stage);
    }

    public static void removeScreenInputProcessor(Stage stage) {
        InputMultiplexer im;
        Object o = Gdx.input.getInputProcessor();
        if (o instanceof InputMultiplexer) {
            im = (InputMultiplexer) o;
        } else {
            im = new InputMultiplexer(stage, (AppScreen) Main.getInstance().getScreen());
            Gdx.input.setInputProcessor(im);
        }

        im.getProcessors().clear();
        im.addProcessor(stage);
    }

}
