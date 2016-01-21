package com.ggwp.interiordesigner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ggwp.interiordesigner.object.AppScreen;

/**
 * Created by Raymond on 1/19/2016.
 */
public class RoomWithHUD extends AppScreen  {
    public static class GameObject extends ModelInstance {
        public final Vector3 center = new Vector3();
        public final Vector3 dimensions = new Vector3();
        public final float radius;

        private final static BoundingBox bounds = new BoundingBox();

        public GameObject(Model model){
            super(model);
            calculateBoundingBox(bounds);
            bounds.getCenter(center);
            bounds.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }

        public GameObject (Model model, String rootNode, boolean mergeTransform) {
            super(model, rootNode, mergeTransform);
            calculateBoundingBox(bounds);
            bounds.getCenter(center);
            bounds.getDimensions(dimensions);
            radius = dimensions.len() / 2f;
        }
    }

    protected PerspectiveCamera cam;
    protected CameraInputController camController;
    protected SpriteBatch batch;
    protected ModelBatch modelBatch;
    protected AssetManager assets;
    protected Array<GameObject> instances = new Array<GameObject>();
    protected Environment environment;
    protected boolean loading;

    protected Array<GameObject> blocks = new Array<GameObject>();
    protected Array<GameObject> invaders = new Array<GameObject>();
    protected ModelInstance ship;
    protected ModelInstance space;

    protected Stage stage;

    private Vector3 position = new Vector3();

    private int selected = -1, selecting = -1;
    private Material selectionMaterial;
    private Material originalMaterial;

    private Texture hudBackgroud;
    private Window catalogWindow;

    private void initEnvironment(){
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initCamera(){
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0f, 7f, 10f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
    }

    private void initInputProcessors(){
        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));
    }

    private void removeScreenInputProcessor(){
        InputMultiplexer im = (InputMultiplexer) Gdx.input.getInputProcessor();
        im.getProcessors().clear();
        stage.addActor(catalogWindow);
        im.addProcessor(stage);
    }

    private Skin defaultSkin;
    private TextButton.TextButtonStyle defaultTextButtonStyle;
    private Texture whiteTexture;
    private Texture blackTexture;

    private void initSkins(){
        defaultSkin = new Skin();
        Pixmap blackPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        blackPixmap.setColor(Color.BLACK);
        blackPixmap.fill();
        blackTexture = new Texture(blackPixmap);

        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.rgba8888(1f, 1f, 1f, .5f));
        whitePixmap.fill();
        whiteTexture = new Texture(whitePixmap);


        defaultSkin.add("defaultButton", new Texture(whitePixmap));
        BitmapFont bitmapFont = new BitmapFont();
        defaultSkin.add("defaultFont", bitmapFont);
        blackPixmap.dispose();
        whitePixmap.dispose();
        defaultTextButtonStyle = new TextButton.TextButtonStyle();
//        defaultTextButtonStyle.up = defaultSkin.newDrawable("defaultButton");
//        defaultTextButtonStyle.down = defaultSkin.newDrawable("defaultButton");
        defaultTextButtonStyle.font = defaultSkin.getFont("defaultFont");
        defaultTextButtonStyle.fontColor = Color.BLACK;
        defaultSkin.add("default", defaultTextButtonStyle);
    }

    private void initHUD(){
        stage = new Stage();
        initSkins();

        hudBackgroud = whiteTexture;

        TextButton addFurniture = new TextButton("+", defaultTextButtonStyle);
        TextButton removeFurniture = new TextButton("x", defaultTextButtonStyle);

        addFurniture.setBounds(Gdx.graphics.getWidth() - 100f, Gdx.graphics.getHeight() - 50f, 40f, 40f);
        removeFurniture.setBounds(Gdx.graphics.getWidth() - 50f, Gdx.graphics.getHeight() - 50f, 40f, 40f);

        addFurniture.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                removeScreenInputProcessor();
            }
        });

        removeFurniture.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected > -1) {
                    GameObject instance = instances.get(selected);
                    if (instance != null) {
                        instances.removeIndex(selected);
                        selected = -1;
                    }
                }
            }
        });

        stage.addActor(addFurniture);
        stage.addActor(removeFurniture);

        initCatalogWindow();
    }

    private void initCatalogWindow(){

        TextButton closeWindow = new TextButton("Close", defaultTextButtonStyle);

        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);

        Window.WindowStyle windowStyle = new Window.WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture)));
        catalogWindow = new Window("", windowStyle);
        catalogWindow.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        catalogWindow.setModal(true);

        TextButton frames = new TextButton("Frames", defaultTextButtonStyle);
        TextButton Bed = new TextButton("Bed with pillow and mattresses", defaultTextButtonStyle);
        TextButton sideTables = new TextButton("Side Tables", defaultTextButtonStyle);
        TextButton vase = new TextButton("Vase", defaultTextButtonStyle);
        TextButton lamps = new TextButton("Lamps", defaultTextButtonStyle);
        TextButton dresser = new TextButton("Dresser Cabinets / Orocan  / Drawers", defaultTextButtonStyle);
        TextButton vanityTables = new TextButton("Vanity Tables and Chairs", defaultTextButtonStyle);
        TextButton sofa = new TextButton("Sofa Set / Couch", defaultTextButtonStyle);
        TextButton coffeeTables = new TextButton("Coffee Tables", defaultTextButtonStyle);
        TextButton tvRack = new TextButton("Tv Rack", defaultTextButtonStyle);
        TextButton bookShelves = new TextButton("Book Shelves", defaultTextButtonStyle);
        TextButton Mirrors = new TextButton("Mirrors( Different Sizes)", defaultTextButtonStyle);
        TextButton diningSet = new TextButton("Dining Set ( Tables and Chairs )", defaultTextButtonStyle);
        TextButton kitchenCabinets = new TextButton("Kitchen Cabinets (dikit sa wall)", defaultTextButtonStyle);
        TextButton wallClock = new TextButton("Wall Clock", defaultTextButtonStyle);
        TextButton tv = new TextButton("TV", defaultTextButtonStyle);
        TextButton washingMachine = new TextButton("Washing Machine", defaultTextButtonStyle);
        TextButton electricFan = new TextButton("Electric Fan", defaultTextButtonStyle);
        TextButton aircon = new TextButton("Aircon", defaultTextButtonStyle);
        TextButton refridgerator = new TextButton("Refridgerator", defaultTextButtonStyle);
        TextButton oven = new TextButton("Oven", defaultTextButtonStyle);
        TextButton oven1 = new TextButton("Oven1", defaultTextButtonStyle);
        TextButton oven2 = new TextButton("Oven2", defaultTextButtonStyle);
        TextButton oven3 = new TextButton("Oven3", defaultTextButtonStyle);
        TextButton oven4 = new TextButton("Oven4", defaultTextButtonStyle);
        TextButton oven5 = new TextButton("Oven5", defaultTextButtonStyle);
        TextButton oven6 = new TextButton("Oven6", defaultTextButtonStyle);
        TextButton oven7 = new TextButton("Oven7", defaultTextButtonStyle);
        TextButton oven8 = new TextButton("Oven8", defaultTextButtonStyle);
        TextButton oven9 = new TextButton("Oven9", defaultTextButtonStyle);
        TextButton oven10 = new TextButton("Oven10", defaultTextButtonStyle);
        TextButton oven11 = new TextButton("Oven11", defaultTextButtonStyle);
        TextButton oven12 = new TextButton("Oven12", defaultTextButtonStyle);
        TextButton oven13 = new TextButton("Oven13", defaultTextButtonStyle);



        Table table = new Table();
//        table.setFillParent(true);
        table.align(Align.left);

        VerticalGroup categories = new VerticalGroup();
        categories.setFillParent(true);
        categories.align(Align.left);


        categories.addActor(frames);
        categories.addActor(Bed);
        categories.addActor(sideTables);
        categories.addActor(vase);
        categories.addActor(lamps);
        categories.addActor(dresser);
        categories.addActor(vanityTables);
        categories.addActor(sofa);
        categories.addActor(coffeeTables);
        categories.addActor(tvRack);
        categories.addActor(bookShelves);
        categories.addActor(Mirrors);
        categories.addActor(diningSet);
        categories.addActor(kitchenCabinets);
        categories.addActor(wallClock);
        categories.addActor(washingMachine);
        categories.addActor(electricFan);
        categories.addActor(aircon);
        categories.addActor(refridgerator);
        categories.addActor(closeWindow);
        categories.addActor(oven);
        categories.addActor(oven1);
        categories.addActor(oven2);
        categories.addActor(oven3);
        categories.addActor(oven4);
        categories.addActor(oven5);
        categories.addActor(oven6);
        categories.addActor(oven7);
        categories.addActor(oven8);
        categories.addActor(oven9);
        categories.addActor(oven10);
        categories.addActor(oven11);
        categories.addActor(oven12);
        categories.addActor(oven13);

        table.add(categories);
        ScrollPane scrollPane = new ScrollPane(table);
        scrollPane.setOverscroll(true,true);
        catalogWindow.add(scrollPane);
        closeWindow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.getActors().removeValue(catalogWindow, true);
                initInputProcessors();
            }
        });
    }

    public RoomWithHUD(){
        modelBatch = new ModelBatch();
        initEnvironment();
        initCamera();
        initHUD();
        Gdx.input.setInputProcessor(new InputMultiplexer(this, stage));

        assets = new AssetManager();
        assets.load("data/invaderscene.g3db", Model.class);
        assets.load("sofa.obj", Model.class);
        loading = true;

        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        originalMaterial = new Material();
    }


    private void doneLoading () {
        Model model = assets.get("data/invaderscene.g3db", Model.class);
        for (int i = 0; i < model.nodes.size; i++) {
            String id = model.nodes.get(i).id;
            GameObject instance = new GameObject(model, id, true);

            if (id.equals("space")) {
                space = instance;
                continue;
            }

            instances.add(instance);

            if (id.equals("ship"))
                ship = instance;
            else if (id.startsWith("block"))
                blocks.add(instance);
            else if (id.startsWith("invader")) invaders.add(instance);
        }

        loading = false;
    }

    @Override
    public void render(float delta) {
    if (loading && assets.update()) doneLoading();
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        for (final GameObject instance : instances) {
            if (isVisible(cam, instance)) {
                modelBatch.render(instance, environment);
            }
        }
        if (space != null) modelBatch.render(space);
        modelBatch.end();

        stage.getBatch().begin();
        stage.getBatch().draw(hudBackgroud, 0, Gdx.graphics.getHeight() - 60f, Gdx.graphics.getWidth(), 60f);
        stage.getBatch().end();
        stage.draw();
    }

    protected boolean isVisible (final Camera cam, final GameObject instance) {
        instance.transform.getTranslation(position);
        position.add(instance.center);
        return cam.frustum.sphereInFrustum(position, instance.radius);
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        selecting = getObject(screenX, screenY);
        return selecting >= 0;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (selecting < 0)
            return false;
        if (selected == selecting) {
            Ray ray = cam.getPickRay(screenX, screenY);
            final float distance = -ray.origin.y / ray.direction.y;
            position.set(ray.direction).scl(distance).add(ray.origin);
            instances.get(selected).transform.setTranslation(position);
        }
        return true;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY))
                setSelected(selecting);
            selecting = -1;
            return true;
        }
        return false;
    }

    public void setSelected (int value) {
        if (selected == value) return;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            mat.clear();
            mat.set(originalMaterial);
        }
        selected = value;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            originalMaterial.clear();
            originalMaterial.set(mat);
            mat.clear();
            mat.set(selectionMaterial);
        }
    }

    public int getObject (int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);

            instance.transform.getTranslation(position);
            position.add(instance.center);

            final float len = ray.direction.dot(position.x-ray.origin.x, position.y-ray.origin.y, position.z-ray.origin.z);
            if (len < 0f)
                continue;

            float dist2 = position.dst2(ray.origin.x+ray.direction.x*len, ray.origin.y+ray.direction.y*len, ray.origin.z+ray.direction.z*len);
            if (distance >= 0f && dist2 > distance)
                continue;

            if (dist2 <= instance.radius * instance.radius) {
                result = i;
                distance = dist2;
            }
        }
        return result;
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }

    @Override
    public void show() {

    }


    @Override
    public void resize (int width, int height) {
//        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause () {
    }

    @Override
    public void resume () {
    }

    @Override
    public void hide() {

    }
}