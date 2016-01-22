package com.ggwp.interiordesigner.object;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ggwp.interiordesigner.manager.SkinManager;


/**
 * Created by Raymond on 1/22/2016.
 */
public class Catalog extends Window {

    protected Stage stage;
    protected AssetManager assets;
    protected Array<ModelInstance> furnitures;
    protected InputMultiplexer inputMultiplexer;
    protected AppScreen appScreen;
    private Catalog instance;

    private ScrollPane categoriesScrollPane;
    private ScrollPane furnituresScrollPane;

    public static Catalog construct(Stage stage, AssetManager assets, Array<ModelInstance> furnitures, InputMultiplexer inputMultiplexer, AppScreen appScreen){
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture texture = new Texture(whitePixmap);
        whitePixmap.dispose();

        Catalog catalog = new Catalog("",new Window.WindowStyle(new BitmapFont(), Color.WHITE, new SpriteDrawable(new Sprite(texture))));
        catalog.stage = stage;
        catalog.assets = assets;
        catalog.furnitures = furnitures;
        catalog.inputMultiplexer = inputMultiplexer;
        catalog.appScreen = appScreen;
        catalog.instance = catalog;
        return catalog;
    }


    public Catalog(String title, WindowStyle style) {
        super(title, style);
        this.setFillParent(true);
        this.setModal(true);
        this.align(Align.left);
        initCategories();
    }

    private void initInputProcessors(){
        inputMultiplexer.getProcessors().clear();
        inputMultiplexer.addProcessor(appScreen);
        inputMultiplexer.addProcessor(stage);
    }

    private Table table = new Table();
    private void initCategories(){
        VerticalGroup categories = new VerticalGroup();

        categoriesScrollPane = new ScrollPane(categories);
        furnituresScrollPane = new ScrollPane(createBedsContainer());

        table.debug();
//        table = new Table();
        table.setFillParent(true);

        table.defaults().left();
        table.columnDefaults(0).width(250f);
        table.columnDefaults(1).top().width(Gdx.graphics.getWidth() - 250f);

        table.add(categoriesScrollPane);
        table.add(furnituresScrollPane);

        EventListener sofaClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Furniture sofa = new Furniture(assets.get("sofa.obj", Model.class));
                sofa.transform.rotate(Vector3.X, -90);
                sofa.calculateTransforms();
                BoundingBox bounds = new BoundingBox();
                sofa.calculateBoundingBox(bounds);
                sofa.shape = new Box(bounds);
                furnitures.add(sofa);
                stage.getActors().removeValue(instance,true);
                initInputProcessors();
            }
        };


        EventListener bedsClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane = new ScrollPane(createBedsContainer());
                table.add(furnituresScrollPane);
            }
        };

        EventListener framesClikListener = new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                furnituresScrollPane = new ScrollPane(createFramesContainer());
                table.add(furnituresScrollPane);
            }
        };

        TextButton.TextButtonStyle defaultTextButtonStyle = SkinManager.getDefaultTextButtonStyle();
        categories.addActor(createCategoryContainer("Bed with pillow/mattresses", "Common/no-image.png", defaultTextButtonStyle, bedsClikListener));
        categories.addActor(createCategoryContainer("Frames","Common/no-image.png",defaultTextButtonStyle,framesClikListener));
        categories.addActor(createCategoryContainer("Side Tables","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Vase","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Lamps", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Dresser Cabinets/Drawers", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Vanity Tables and Chairs", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Sofa Set/Couch", "Common/no-image.png", defaultTextButtonStyle, sofaClikListener));
        categories.addActor(createCategoryContainer("Coffee Tables","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Tv Rack","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Book Shelves","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Mirrors","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Dining Set","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Kitchen Cabinets","Common/no-image.png",defaultTextButtonStyle,null));
        categories.addActor(createCategoryContainer("Wall Clock", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("TV", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Washing Machine", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Electric Fan", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Aircon", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Refridgerator", "Common/no-image.png", defaultTextButtonStyle, null));
        categories.addActor(createCategoryContainer("Oven", "Common/no-image.png", defaultTextButtonStyle, null));




        this.add(table);
    }

    private Table createCategoryContainer(String label, String img, TextButton.TextButtonStyle buttonStyle, EventListener listener) {
        Table table = new Table();
        Image image = new Image((new Texture(img)));
        TextButton button = new TextButton(label,buttonStyle);
        button.getLabel().setColor(Color.WHITE);
        button.getLabel().setAlignment(Align.left);

        if(listener != null){
            button.addListener(listener);
        }

        table.columnDefaults(0).center().pad(10f);
        table.columnDefaults(1).left().padRight(10f);

        table.add(image).width(40f).height(40f);
        table.add(button).width(180f).height(40f);
        return table;
    }

    private Container createBedsContainer(){
        Table main = new Table();
        main.setDebug(true);
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Bed with pillow/mattresses", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("1", "Common/no-image.png", null));
        main.add(createFurnitureCard("2", "Common/no-image.png", null));
        main.add(createFurnitureCard("3", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("4", "Common/no-image.png", null));
        main.add(createFurnitureCard("5", "Common/no-image.png", null));
        main.add(createFurnitureCard("6", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("7", "Common/no-image.png", null));
        main.add(createFurnitureCard("8", "Common/no-image.png", null));
        main.add(createFurnitureCard("9", "Common/no-image.png", null));

        return new Container(main);
    }

    private Container createFramesContainer(){
        Table main = new Table();
        main.setDebug(true);
        main.setFillParent(true);
        main.defaults().left().pad(20f).padRight(0);
        main.columnDefaults(2).padRight(20f);
        main.add(new Label("Frames", SkinManager.getDefaultLabelStyle())).colspan(3);
        main.row();

        main.add(createFurnitureCard("1", "Common/no-image.png", null));
        main.add(createFurnitureCard("2", "Common/no-image.png", null));
        main.add(createFurnitureCard("3", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("4", "Common/no-image.png", null));
        main.add(createFurnitureCard("5", "Common/no-image.png", null));
        main.add(createFurnitureCard("6", "Common/no-image.png", null));
        main.row();
        main.add(createFurnitureCard("7", "Common/no-image.png", null));
        main.add(createFurnitureCard("8", "Common/no-image.png", null));
        main.add(createFurnitureCard("9", "Common/no-image.png", null));

        return new Container(main);
    }

    private Container createFurnitureCard(String name,String img, EventListener listener){
        Table main = new Table();
        Image image = new Image((new Texture(img)));

        float cardSize = ((Gdx.graphics.getWidth()-250f) / (3f)) - 30f;

        main.add(image).width(cardSize).height(cardSize);

        main.row();
        main.add(new Label(name, SkinManager.getDefaultLabelStyle()));
        return new Container(main);
    }
}