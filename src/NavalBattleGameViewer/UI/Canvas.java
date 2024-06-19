package NavalBattleGameViewer.UI;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class Canvas<E extends Enum<E>> {

    public void setFocus(E element) {
        if (focusableElementsMap.containsKey(element)) {
            if (focusedElement != null) {
                this.focusableElementsMap.get(focusedElement).processEvent(UIevents.ExitedArea);
            }
            focusedElement = element;
            this.focusableElementsMap.get(focusedElement).processEvent(UIevents.EnteredArea);
        }
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

}
