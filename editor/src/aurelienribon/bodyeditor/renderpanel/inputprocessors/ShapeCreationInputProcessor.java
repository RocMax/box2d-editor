package aurelienribon.bodyeditor.renderpanel.inputprocessors;

import aurelienribon.bodyeditor.AppManager;
import aurelienribon.bodyeditor.models.ShapeModel;
import aurelienribon.bodyeditor.renderpanel.RenderPanel;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ShapeCreationInputProcessor extends InputAdapter {
	boolean isActive = false;

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		boolean isValid = button == Buttons.LEFT && InputHelper.isShapeCreationKeyDown();

		if (!isValid)
			return false;
		isActive = true;

		if (!AppManager.instance().isCurrentModelValid())
			return true;

		ShapeModel lastShape = AppManager.instance().getLastTempShape();

		if (lastShape == null || lastShape.isClosed())
			lastShape = AppManager.instance().createNewTempShape();

		if (lastShape.getPointCount() >= 3 && AppManager.instance().nearestPoint == lastShape.getPoint(0)) {
			lastShape.close();
			AppManager.instance().saveCurrentModel();
		} else {
			Vector2 p = RenderPanel.instance().alignedScreenToWorld(x, y);
			lastShape.addPoint(p);
		}

		return true;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		if (!isActive)
			return false;
		isActive = false;
		return true;
	}

	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		if (!isActive)
			return false;
		touchMoved(x, y);
		return true;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		if (!AppManager.instance().isCurrentModelValid())
			return false;

		// Nearest point computation
		Vector2 p1 = RenderPanel.instance().screenToWorld(x, y);
		AppManager.instance().nearestPoint = null;
		ShapeModel shape = AppManager.instance().getLastTempShape();
		if (shape != null && !shape.isClosed() && shape.getPointCount() >= 3)
			if (shape.getPoint(0).dst(p1) < 10 * RenderPanel.instance().getCamera().zoom)
				AppManager.instance().nearestPoint = shape.getPoint(0);

		// Next point assignment
		Vector2 p2 = RenderPanel.instance().alignedScreenToWorld(x, y);
		AppManager.instance().nextPoint = p2;
		return false;
	}
}
