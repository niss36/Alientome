package com.alientome.core.internationalization;

import com.alientome.core.settings.Config;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Labeled;

import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * I18N stands for Internationalization. This class is used to provide ways to access pre-translated Strings with their
 * identifiers. Language can also be changed dynamically, through the locale property. GUI text items can be bound to this instance
 * in order to have their text updated dynamically on language change.
 * Under normal circumstances, the global I18N instance should be shared through the {@link com.alientome.core.SharedInstances} class.
 * Implementations are required to bind the locale property with the config's language property on load.
 */
public interface I18N {

    /**
     * Initializes this instance, and load all required resource files.
     * Also binds to the Config's language property.
     * @implNote The {@link Config} instance must be created, initialized and shared before this method is called.
     */
    void load();

    /**
     * @return The {@link Locale} property that tracks the current language.
     */
    ObjectProperty<Locale> localeProperty();

    /**
     * Gets the localized String for the given unlocalized key, and formats it with the given arguments.
     *
     * @param key the unlocalized identifier.
     * @param args an array of arguments used to format the resulting, localized string. Can be empty.
     * @return A localized string, formatted with given arguments if present.
     */
    String get(String key, Object... args);

    /**
     *
     * @param key
     * @param args
     * @return
     */
    StringBinding createStringBinding(String key, Object... args);

    /**
     * Creates a binding that will be updated on locale or dependency change.
     * The provided function is intended to use this instance's {@link #get(String, Object...)} method to compute
     * its result.
     *
     * @param function a string-returning function that is used to compute an unlocalized string, using the given
     *                 dependencies. Its value will be computed each time the binding is invalidated, that is on
     *                 locale or dependency change.
     * @param dependencies an array of Observable objects, which affect the function and should trigger an invalidation
     *                     on the binding when changed.
     * @return A StringBinding reflecting the function's value.
     */
    StringBinding createStringBinding(Callable<String> function, Observable... dependencies);

    /**
     * Binds the given Labeled component to this instance. The component's current text is used as the unlocalized key.
     * Thereafter, the component's text will be updated according to changes in language to display the correct string.
     *
     * @param labeled the Labeled component whose text property is to bind.
     */
    void applyBindTo(Labeled labeled);

    /**
     * Binds the provided Labeled component's text property with a new property, that uses the dependencies as format arguments.
     * For example, consider 'text=Hello %s ! It is %d pm' as an internationalized string. If a Label is initialized with text
     * "text", and bound through this method with a StringProperty and an IntegerProperty, its text will be updated on locale change,
     * but also on each properties' change, and the two values will be used as format arguments. Therefore, if the StringProperty's
     * value is "John Doe" and the IntegerProperty's value is 8, the Label's text will be "Hello John Doe ! It is 8 pm".
     *
     * @param labeled the component whose text property is to bind. The initial text is used as a resource key.
     * @param dependencies the observable dependencies to be used as arguments when formatting the text.
     */
    void applyBindTo(Labeled labeled, ObservableValue<?>... dependencies);

    /**
     * Fast-path method.
     *
     * @see #applyBindTo(Labeled, ObservableValue[])
     */
    void applyBindTo(Labeled labeled, ObservableValue<?> dependency);

    /**
     * Fast-path method.
     *
     * @see #applyBindTo(Labeled, ObservableValue[])
     */
    void applyBindTo(Labeled labeled, ObservableValue<?> dependency1, ObservableValue<?> dependency2);
}
