package com.enliple.keyboard.imgmodule.module;

import com.enliple.keyboard.imgmodule.ImageBuilder;
import com.enliple.keyboard.imgmodule.Registry;

/**
 * An interface allowing lazy configuration of ImageModule including setting options using {@link
 * ImageBuilder} and registering {@link com.enliple.keyboard.imgmodule.load.model.ModelLoader
 * ModelLoaders}.
 *
 * <p>To use this interface:
 *
 * <ol>
 *   <li>Implement the ImageModule interface in a class with public visibility, calling {@link
 *       Registry#prepend(Class, Class, com.enliple.keyboard.imgmodule.load.ResourceDecoder)} for each {@link
 *       com.enliple.keyboard.imgmodule.load.model.ModelLoader} you'd like to register:

 * </ol>
 *
 * <p>All implementations must be publicly visible and contain only an empty constructor so they can
 * be instantiated via reflection when ImageModule is lazily initialized.
 *
 * <p>There is no defined order in which modules are called, so projects should be careful to avoid
 * applying conflicting settings in different modules. If an application depends on libraries that
 * have conflicting modules, the application should consider avoiding the library modules and
 * instead providing their required dependencies in a single application module.
 *
 * @deprecated Libraries should use {@link LibraryImageModule} and Applications should use {@link
 *     AppImageModule}.
 */
@Deprecated
public interface ImageModule extends RegistersComponents, AppliesOptions {}
