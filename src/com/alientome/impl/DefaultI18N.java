package com.alientome.impl;

import com.alientome.core.SharedInstances;
import com.alientome.core.internationalization.I18N;
import com.alientome.core.settings.Config;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Labeled;
import javafx.util.StringConverter;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import static com.alientome.core.SharedNames.CONFIG;

/**
 * The default implementation for the {@link I18N} interface.
 */
public class DefaultI18N implements I18N {

    /**
     * The base name for all resource files.
     */
    private final String baseName;

    /**
     * The locale property.
     * @see #localeProperty()
     */
    private final ObjectProperty<Locale> locale;

    /**
     * A property tracking the current resource bundle.
     * Changes on the locale property are reflected here.
     */
    private final ObjectProperty<ResourceBundle> bundle;

    public DefaultI18N(String baseName) {

        this.baseName = baseName;

        locale = new SimpleObjectProperty<>();
        locale.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
        bundle = new SimpleObjectProperty<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load() {

        Config config = SharedInstances.get(CONFIG);

        Property<String> languageProperty = config.getProperty("language");

        localeProperty().set(new Locale(languageProperty.getValue()));

        bundle.set(ResourceBundle.getBundle(baseName, localeProperty().get()));

        Bindings.bindBidirectional(languageProperty, localeProperty(), new StringConverter<Locale>() {
            @Override
            public String toString(Locale object) {
                return object.getLanguage();
            }

            @Override
            public Locale fromString(String string) {
                Locale l = new Locale(string);
                bundle.set(ResourceBundle.getBundle(baseName, l));
                return l;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String get(String key, Object... args) {
        return String.format(bundle.get().getString(key), args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringBinding createStringBinding(String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), localeProperty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringBinding createStringBinding(Callable<String> function, Observable... dependencies) {

        Observable[] allDependencies = new Observable[dependencies.length + 1];
        allDependencies[0] = locale;
        System.arraycopy(dependencies, 0, allDependencies, 1, dependencies.length);

        return Bindings.createStringBinding(function, allDependencies);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyBindTo(Labeled labeled) {
        labeled.textProperty().bind(createStringBinding(labeled.getText()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyBindTo(Labeled labeled, ObservableValue<?>... dependencies) {

        String text = labeled.getText();

        labeled.textProperty().bind(createStringBinding(() -> {
            Object[] args = new Object[dependencies.length];
            for (int i = 0; i < args.length; i++)
                args[i] = dependencies[i].getValue();
            return get(text, args);
        }, dependencies));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyBindTo(Labeled labeled, ObservableValue<?> dependency) {

        String text = labeled.getText();
        labeled.textProperty().bind(createStringBinding(() -> get(text, dependency.getValue()), dependency));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void applyBindTo(Labeled labeled, ObservableValue<?> dependency1, ObservableValue<?> dependency2) {

        String text = labeled.getText();
        labeled.textProperty().bind(createStringBinding(() -> get(text, dependency1.getValue(), dependency2.getValue()), dependency1, dependency2));
    }
}
