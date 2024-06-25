package NavalBattleGameViewer.UI;

import NavalBattleGameViewer.Coord2D;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Canvas<E extends Enum<E>> {

    public Canvas(int width, int height) {
        this.canvasSize.x = width;
        this.canvasSize.y = height;
    }

    public void setFocus(E element) {
        if (focusableElementsMap.containsKey(element)) {
            if (focusedElement != null) {
                this.focusableElementsMap.get(focusedElement).invokeEvent(UIevents.ExitedArea);
            }
            focusedElement = element;
            this.focusableElementsMap.get(focusedElement).invokeEvent(UIevents.EnteredArea);
        }
    }

    public void clearFocus() {
        this.focusableElementsMap.get(focusedElement).invokeEvent(UIevents.ExitedArea);
    }

    public ArrayList<UIElement> getUIElementsList() {
        return new ArrayList<>(this.UIElementsMap.values());
    }

    public <T extends UIElement> ArrayList<T> getElementsOfType(Class<T> elementType) {
        ArrayList<T> elementsOfType = new ArrayList<>();

        for (var element: this.getUIElementsList()) {
            if (elementType.isInstance(element)) {
                elementsOfType.add(elementType.cast(element));
            }
        }

        return elementsOfType;
    }

    public UIElement getFocusedElement() {
        return focusableElementsMap.get(focusedElement);
    }

    protected E focusedElement;

    protected HashMap<E, UIElement> UIElementsMap = new HashMap<>();
    protected HashMap<E, UIElement> focusableElementsMap = new HashMap<>();

    protected Coord2D canvasSize = new Coord2D();

}
