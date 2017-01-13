package io.github.teamfractal.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricStaggeredTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.teamfractal.RoboticonQuest;

public class GameScreen implements Screen {
	private final RoboticonQuest game;
	private final OrthographicCamera camera;
	private final Stage stage;
	private final Table table;
	private IsometricStaggeredTiledMapRenderer renderer;
	private TiledMap tmx;
	private TextButton currentButton;
	private TextButton nextButton;
	private Label topText;
	private Label playerStats;
	private float oldX;
	private float oldY;

	private float oldW;
	private float oldH;
	private boolean buttonNotPressed = true;
	

	/**
	 * Initialise the class
	 * @param game  The game object
	 */
	public GameScreen(final RoboticonQuest game) {
		oldW = Gdx.graphics.getWidth();
		oldH = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, oldW, oldH);
		camera.update();


		this.game = game;

		// TODO: Add some HUD gui stuff (buttons, mini-map etc...)
		this.stage = new Stage(new ScreenViewport());
		this.table = new Table();
		stage.addActor(table);
		nextButton = new TextButton("Next Phase", game.skin);
		nextButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (currentButton != null) currentButton.remove();
				game.nextPhase();
				topTextUpdate();
				playerStatsUpdate();
			}
		});
		nextButton.setPosition(stage.getViewport().getWorldWidth() - 80, 0);
		stage.addActor(nextButton);
		topTextUpdate();
		playerStatsUpdate();
		
		

		// Drag the map within the screen.
		stage.addListener(new DragListener() {
			/**
			 * On start of the drag event, record current position.
			 * @param event    The event object
			 * @param x        X position of mouse (on screen)
			 * @param y        Y position of mouse (on screen)
			 * @param pointer  unknown argument, not used.
			 */
			@Override
			public void dragStart(InputEvent event, float x, float y, int pointer) {
				oldX = x;
				oldY = y;
			}

			/**
			 * During the drag event, check against last recorded
			 * mouse positions, apply the offset in an opposite
			 * direction to the camera to create the drag effect.
			 *
			 * @param event    The event object.
			 * @param x        X position of mouse (on screen)
			 * @param y        Y position of mouse (on screen)
			 * @param pointer  unknown argument, not used.
			 */
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				float deltaX = x - oldX;
				float deltaY = y - oldY;

				// The camera translates in a different direction...
				camera.translate(-deltaX, -deltaY);
				if (camera.position.x < 20) camera.position.x = 20;
				if (camera.position.y < 20) camera.position.y = 20;
				if (camera.position.x > 10000) camera.position.x = 10000;
				if (camera.position.y > 10000) camera.position.y = 10000;
				if (currentButton != null){
					if(camera.position.x > 20 && camera.position.x < 10000){
						currentButton.setPosition(currentButton.getX() + deltaX, currentButton.getY());
					}
					if (camera.position.y > 20 && camera.position.y < 10000){
						currentButton.setPosition(currentButton.getX(), currentButton.getY() + deltaY);
						}
					currentButton.remove();
					stage.addActor(currentButton);
				}
				

				// Record cords
				oldX = x;
				oldY = y;
			}
		});

		// Set initial camera position.
		camera.position.x = 20;
		camera.position.y = 50;

		//<editor-fold desc="Click event handler. Check `tileClicked` for how to handle tile click.">
		// Bind click event.
		stage.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// The Y from screen starts from bottom left.
				Vector3 cord = new Vector3(x, oldH - y, 0);
				camera.unproject(cord);

				// Padding offset
				cord.y -= 20;  // Padding from tile
				cord.x += 50;

				// Convert to grid index
				// http://2dengine.com/doc/gs_isometric.html
				TiledMapTileLayer layer = (TiledMapTileLayer)tmx.getLayers().get(0);
				TiledMapTileLayer layer2 = (TiledMapTileLayer)tmx.getLayers().get(2);
				
				float tile_height = layer.getTileHeight();
				float tile_width = layer.getTileWidth();

				float ty = cord.y - cord.x/2 - tile_height;
				float tx = cord.x + ty;
				ty = MathUtils.ceil(-ty/(tile_width/2));
				tx = MathUtils.ceil(tx/(tile_width/2)) + 1;
				int cordX = MathUtils.floor((tx + ty)/2);
				int cordY = -(int)(ty - tx);


				// TODO: Remove those magic numbers and fix it properly
				// The magic numbers based on observation of number patterns
				cordX -= 1;
				if (cordY % 2 == 0) {
					cordX --;
				}

				TiledMapTileLayer.Cell c = layer.getCell(cordX, cordY);
				TiledMapTileLayer.Cell c2 = layer2.getCell(cordX, cordY);
				if (c != null) {
					GameScreen.this.tileClicked(c, c2, x, y);
				}
			}
		});
		//</editor-fold>

		// Finally, start a new game and initialise variables.
		newGame();
	}

	/**
	 * TileCell been clicked
	 * @param cell  The cell clicked
	 * @param x     The x index
	 * @param y     The y index
	 */
	private void tileClicked(final TiledMapTileLayer.Cell cell, final TiledMapTileLayer.Cell cell2,  float mouseX, float mouseY) {
		// TODO: Need proper event callback
		if (currentButton != null) {
				currentButton.remove();
			}
		if (game.getPhase() == 1 && buttonNotPressed){
			
			currentButton = new TextButton("buy landplot", game.skin);
			currentButton.setPosition(mouseX, mouseY);
			currentButton.addListener(new ChangeListener() {
				

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					if (currentButton != null) currentButton.remove();
					game.getPlayer().purchaseLandPlot();
					cell2.setTile(tmx.getTileSets().getTile(67 + game.getPlayerInt()));
					playerStatsUpdate();
					buttonNotPressed = false;
					currentButton = null;
				}
			});
			stage.addActor(currentButton);
			
		}
		if (! buttonNotPressed){
			buttonNotPressed = true;
		}
		
	}
	public void topTextUpdate(){
		if (this.topText != null) this.topText.remove();
		String text = "Player " + (game.getPlayerInt() + 1) + "; Phase " + game.getPhase();
		this.topText = new Label(text, game.skin);
		topText.setWidth(120);
		topText.setPosition(stage.getViewport().getWorldWidth()/2, stage.getViewport().getWorldHeight() - 20);
		stage.addActor(topText);
	}
	
	public void playerStatsUpdate(){
		if (this.playerStats != null) this.playerStats.remove();
		String text = "Ore: " + game.getPlayer().getOre() + " Energy: " +  game.getPlayer().getEnergy() + " Food: "
				+ game.getPlayer().getFood() + " Money: " + game.getPlayer().getMoney();
		this.playerStats = new Label(text, game.skin);
		playerStats.setWidth(250);
		playerStats.setPosition(0, stage.getViewport().getWorldHeight() - 20);
		stage.addActor(playerStats);
	}
	/**
	 * Reset to new game status.
	 */
	public void newGame() {
		// Setup the game board.
		if (tmx != null) tmx.dispose();
		if (renderer != null) renderer.dispose();

		tmx = new TmxMapLoader().load("tiles/city.tmx");
		renderer = new IsometricStaggeredTiledMapRenderer(tmx);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		renderer.setView(camera);
		renderer.render();

		stage.act(delta);
		stage.draw();
	}

	/**
	 * Resize the viewport as the render window's size change.
	 * @param width   The new width
	 * @param height  The new height
	 */
	@Override
	public void resize(int width, int height) {
		// Avoid the viewport update if they are not changed.
		if (width != oldW && height != oldH) {
			stage.getViewport().update(width, height, true);
			camera.setToOrtho(false, width, height);
			topTextUpdate();
			playerStatsUpdate();
			nextButton.setPosition(stage.getViewport().getWorldWidth() - 80, 0);
			oldW = width;
			oldH = height;
		}
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		tmx.dispose();
		renderer.dispose();
		stage.dispose();
	}
}