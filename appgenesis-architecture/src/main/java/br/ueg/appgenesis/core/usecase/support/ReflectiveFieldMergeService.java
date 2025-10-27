package br.ueg.appgenesis.core.usecase.support;

import br.ueg.appgenesis.core.domain.annotation.Field;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectiveFieldMergeService implements FieldMergeService {

    @Override
    public <T> void merge(T source, T target, MergeMode mode) {
        if (source == null || target == null) return;
        Class<?> clazz = target.getClass();

        for (java.lang.reflect.Field f : allFields(clazz)) {
            try {
                if (Modifier.isStatic(f.getModifiers())) continue;

                // Nunca sobrescreve ID via update de dados externos
                //TODO conferir depois para id nãõ gerado
                if (f.getName().equalsIgnoreCase("id")) continue;

                // Blindagem por anotação
                Field meta = f.getAnnotation(Field.class);
                if (meta != null && meta.internalData()) continue;

                f.setAccessible(true);

                Object srcVal = getValue(source, f);
                Object tgtVal = getValue(target, f);

                switch (mode) {
                    case REPLACE_ALL:
                        // PUT: substitui tudo, inclusive null (exceto internos/ID)
                        setValue(target, f, srcVal);
                        break;

                    case PRESENT_ONLY:
                        // PATCH: aplica apenas valores não nulos
                        if (srcVal != null) setValue(target, f, srcVal);
                        else setValue(target, f, tgtVal); // mantém valor atual
                        break;
                }

            } catch (Exception ignored) {
                // Pode logar se desejar
            }
        }
    }

    private Object getValue(Object obj, java.lang.reflect.Field field) throws Exception {
        String name = field.getName();
        String capitalized = capitalize(name);

        try {
            return obj.getClass().getMethod("get" + capitalized).invoke(obj);
        } catch (NoSuchMethodException e) {
            try {
                return obj.getClass().getMethod("is" + capitalized).invoke(obj);
            } catch (NoSuchMethodException ex) {
                field.setAccessible(true);
                return field.get(obj);
            }
        }
    }

    private void setValue(Object obj, java.lang.reflect.Field field, Object value) throws Exception {
        String name = field.getName();
        String capitalized = capitalize(name);

        try {
            obj.getClass().getMethod("set" + capitalized, field.getType()).invoke(obj, value);
        } catch (NoSuchMethodException e) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private List<java.lang.reflect.Field> allFields(Class<?> type) {
        List<java.lang.reflect.Field> out = new ArrayList<>();
        Class<?> cur = type;
        while (cur != null && cur != Object.class) {
            for (java.lang.reflect.Field f : cur.getDeclaredFields()) {
                out.add(f);
            }
            cur = cur.getSuperclass();
        }
        return out;
    }
}
