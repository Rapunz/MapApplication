

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class MapDisplay extends Pane {

	private final Map<Category, Set<Place>> byCategory = new EnumMap<>(Category.class);
	private final Map<String, Set<Place>> byName = new HashMap<>();
	private final Map<Position, Place> byPosition = new HashMap<>();
	private final ArrayList<Place> markedList = new ArrayList<>();
	private ImageView imageView = new ImageView();
	private EventHandler<MouseEvent> placeClickHandler;
	private boolean changed;

	public MapDisplay(Image mapImage) {
		getChildren().add(imageView);
		imageView.setImage(mapImage);
	}

	public void setOnPlaceClicked(EventHandler<MouseEvent> handler) {
		placeClickHandler = handler;
		if (!byPosition.isEmpty()) {
			for (Place p : byPosition.values()) {
				p.setOnMouseClicked(handler);
			}
		}
	}

	public Place get(Position position) {
		return byPosition.get(position);
	}

	public void add(Place place) {
		addToCategoryMap(place);
		addToNameMap(place);
		byPosition.put(place.getPosition(), place);
		getChildren().add(place);
		place.setOnMouseClicked(placeClickHandler);
		changed = true;
	}

	private void addToCategoryMap(Place place) {
		Category category = place.getCategory();
		if (category != Category.NONE) {
			Set<Place> places = byCategory.get(category);
			if (places == null) {
				places = new HashSet<>();
				byCategory.put(category, places);
			}

			places.add(place);
		}
	}

	private void addToNameMap(Place place) {
		String name = place.getName();
		Set<Place> places = byName.get(name);
		if (places == null) {
			places = new HashSet<>();
			byName.put(name, places);
		}
		places.add(place);
	}

	public void remove(Place place) {
		Collection<Place> rightCat = byCategory.get(place.getCategory());
		if (rightCat != null) {
			rightCat.remove(place);
		}
		Collection<Place> rightName = byName.get(place.getName());
		if (rightName != null) {
			rightName.remove(place);
		}
		byPosition.remove(place.getPosition());
		markedList.remove(place);
		changed = true;
	}

	public void removeMarked() {
		getChildren().removeAll(markedList);
		for (int i = markedList.size() - 1; i >= 0; i--) {
			remove(markedList.get(i));
		}
	}

	public void mark(Place place) {
		if (!markedList.contains(place)) {
			markedList.add(place);
			place.setMarked(true);
			show(place);
		}
	}

	public void mark(String name) {
		Collection<Place> matching = byName.get(name);
		if (matching != null) {
			for (Place match : matching) {
				mark(match);
			}
		}
	}

	public void unmark(Place place) {
		markedList.remove(place);
		place.setMarked(false);
	}

	public void unmarkAll() {
		for (int i = markedList.size() - 1; i >= 0; i--) {
			unmark(markedList.get(i));
		}
	}

	public void toggleMarked(Place place) {
		if (place.isMarked()) {
			unmark(place);
		} else {
			mark(place);
		}
	}

	public void hide(Place place) {
		unmark(place);
		place.setVisible(false);
	}

	public void hide(Category category) {
		Collection<Place> places = byCategory.get(category);
		if (places != null) {
			for (Place place : places) {
				hide(place);
			}
		}
	}

	public void hideMarked() {
		for (int i = markedList.size() - 1; i >= 0; i--) {
			hide(markedList.get(i));
		}
	}

	public void show(Place place) {
		place.setVisible(true);
	}

	public void show(Category category) {
		Collection<Place> places = byCategory.get(category);
		if (places != null) {
			for (Place place : places) {
				show(place);
			}
		}
	}

	public boolean hasChanged() {
		return changed;
	}

	public void save() {
		changed = false;
	}

	public void clear() {
		byCategory.clear();
		byName.clear();
		byPosition.clear();
		markedList.clear();
		changed = false;
		getChildren().clear();
		getChildren().add(imageView);
	}

	public String getSaveFormat() {
		String string = "";
		for (Place place : byPosition.values()) {
			string += place.getSaveFormat() + "\n";
		}
		return string;
	}
}
