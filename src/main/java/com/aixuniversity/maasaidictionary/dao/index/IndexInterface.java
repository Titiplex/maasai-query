// dao/index/SearchIndex.java
package main.java.com.aixuniversity.maasaidictionary.dao.index;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import main.java.com.aixuniversity.maasaidictionary.dao.normal.AbstractDao;
import main.java.com.aixuniversity.maasaidictionary.model.AbstractModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

/**
 * Contrat minimal : construire un index et renvoyer la (petite) posting‑list.
 * Chaque nouvelle dimension de recherche (tonalités, traits prosodiques…) se code en < 50 lignes.
 */
public interface IndexInterface<T extends AbstractModel> {

    int GENERIC_INTERFACE_INDEX = 0;
    int TYPE_ARGUMENT_INDEX = 0;

    @SuppressWarnings("unchecked")
    private Class<T> getTClass() {
        return (Class<T>) extractGenericTypeArgument(getClass());
    }

    private static Class<?> extractGenericTypeArgument(Class<?> clazz) {
        Type genericInterface = clazz.getGenericInterfaces()[IndexInterface.GENERIC_INTERFACE_INDEX];
        if (!(genericInterface instanceof ParameterizedType parameterizedType)) {
            throw new IllegalStateException("No generic type arguments found");
        }
        Type typeArgument = parameterizedType.getActualTypeArguments()[IndexInterface.TYPE_ARGUMENT_INDEX];
        if (!(typeArgument instanceof Class<?>)) {
            throw new IllegalStateException("Type argument is not a Class");
        }
        return (Class<?>) typeArgument;
    }

    sealed interface Token permits Token.StringToken, Token.IntegerToken {
        record StringToken(String value) implements Token {
        }

        record IntegerToken(Integer value) implements Token {
        }

        static Token of(String value) {
            return new StringToken(value);
        }

        static Token of(Integer value) {
            return new IntegerToken(value);
        }
    }

    /**
     * @param token clef primaire (phonème, catégorie, tonalité…).
     * @return liste éventuellement vide d’IDs de Vocabulary.
     */
    IntArrayList idsFor(T token);

    IntArrayList idsFor(Token token);

    /**
     * Fréquence brute (sert à choisir le pivot).
     */
    int frequency(T token);

    int frequency(Token token);

    void updateFrequency(Object token, IntArrayList list);

    default Map<T, Integer> index(AbstractDao<T> dao) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException, SQLException {
        Collection<T> items = fetchItemsFromModel();
        return dao.insertAll(items);
    }

    private Collection<T> fetchItemsFromModel() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, InstantiationException {
        final String GET_PREFIX = "get";
        final String LIST_SUFFIX = "List";

        Class<T> modelClass = getTClass();
        T modelInstance = modelClass.getDeclaredConstructor().newInstance();
        String methodName = GET_PREFIX + modelClass.getSimpleName() + LIST_SUFFIX;
        Method getListMethod = modelClass.getMethod(methodName);

        @SuppressWarnings("unchecked")
        Collection<T> result = (Collection<T>) getListMethod.invoke(modelInstance);
        return result;
    }
}