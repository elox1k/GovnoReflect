package com.refleks;

import com.refleks.*;
import com.refleks.modules.Fly;

import java.io.IOException;
import java.util.ArrayList;

import java.util.function.Predicate;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ModuleRegistrator {
private static ArrayList<Module> modules = new ArrayList<Module>();
private static boolean cheatEnabled = true;
private static boolean prevState = false;
private static boolean state;
static {
    try {
        // добавляем модули через рефлексию
        Class[] moduleClasses = {Fly.class};
        for (Class moduleClass : moduleClasses) {
            Constructor<Module> constructor = moduleClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            Module module = constructor.newInstance();
            modules.add(module);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static void registerModule(Module e) {
    modules.add(e);
}

public static ArrayList<Module> getModules() {
    return modules;
}

public void setState(boolean state) {
    this.state = state;
}

public void setFunctionEnabled(String functionName) {
    try {
        // получаем метод setEnabled(String) у объекта Module
        Method setEnabledMethod = Module.class.getDeclaredMethod("setEnabled", boolean.class);
        setEnabledMethod.setAccessible(true);

        // получаем объект Module по названию
        Module module = getModuleByString(functionName);

        // вызываем метод setEnabled(true) на объекте Module
        setEnabledMethod.invoke(module, true);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

public boolean isFunctionEnabled(String functionName) {
    try {
        // получаем метод getState() у объекта Module
        Method getStateMethod = Module.class.getDeclaredMethod("getState");
        getStateMethod.setAccessible(true);

        // получаем объект Module по названию
        Module module = getModuleByString(functionName);

        // вызываем метод getState() на объекте Module
        return (Boolean) getStateMethod.invoke(module);
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

public Module getModule(Class<? extends Module> clazz) {
    for (Module module : modules) {
        if (clazz.isInstance(module)) {
            return module;
        }
    }
    return null;
}


public Module getModuleByString(final String moduleName) {
    try {
        // получаем метод getName() у объекта Module
        Method getNameMethod = Module.class.getDeclaredMethod("getName");
        getNameMethod.setAccessible(true);

        // ищем объект Module с таким же названием
        for (Module module : modules) {
            String name = (String) getNameMethod.invoke(module);
            if (name.equalsIgnoreCase(moduleName)) {
                return module;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

	public static void onKeyInput(int key) {
	    if (Minecraft.getMinecraft().currentScreen != null) {
	        return;
	    }

	    try {
	        for (Module mod : getModules()) {
	            Method getBindMethod = mod.getClass().getMethod("getBind");
	            int bind = (int) (Integer) getBindMethod.invoke(mod);
	            if (bind == key) {
	                Method toggleMethod = mod.getClass().getMethod("toggle");
	                toggleMethod.invoke(mod);
	            }
	        }
	    }  catch (IllegalAccessException e) {
	        e.printStackTrace();
	    } catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}


	
public static void onTick(TickEvent.ClientTickEvent event) {
	try {
		if (!state) {
			cheatEnabled = !cheatEnabled;
		}
		for (Module module : getModules()) {
			if (module.isEnabled()) {
				Method method = module.getClass().getDeclaredMethod("onTick", TickEvent.ClientTickEvent.class);
				method.invoke(module, event);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}



public void onRender() {
	try {
		for (Module module : getModules()) {
			Method method = module.getClass().getDeclaredMethod("onRender");
			method.invoke(module);
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}

public void onRender3D(RenderWorldLastEvent event) {
	try {
		for (Module module : getModules()) {
			if (module.isEnabled()) {
				Method method = module.getClass().getDeclaredMethod("onRender3D", RenderWorldLastEvent.class);
				method.invoke(module, event);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}

public void onRenderWorld(RenderWorldLastEvent event) {
	try {
		for (Module module : getModules()) {
			Method method = module.getClass().getDeclaredMethod("onWorldRender", RenderWorldLastEvent.class);
			Object minecraft = Class.forName("net.minecraft.client.Minecraft").getMethod("getMinecraft").invoke(null);
			Object theWorld = minecraft.getClass().getMethod("theWorld").invoke(minecraft);
			if (!(!module.isEnabled() || theWorld == null)) {
				method.invoke(module, event);
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}

}
