package com.vriza.donasikita.utils;

import com.vriza.donasikita.models.Category;
import com.vriza.donasikita.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;

public class CategoryManager {
    private static CategoryManager instance;
    private List<Category> categories;
    private CategoryRepository repository;

    private CategoryManager() {
        repository = new CategoryRepository();
        categories = new ArrayList<>();
    }

    public static CategoryManager getInstance() {
        if (instance == null) {
            instance = new CategoryManager();
        }
        return instance;
    }

    public interface CategoryLoadListener {
        void onCategoriesLoaded(List<Category> categories);
        void onError(String errorMessage);
    }

    public void loadCategories(CategoryLoadListener listener) {
        repository.getCategories(new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> loadedCategories) {
                categories.clear();
                categories.addAll(loadedCategories);
                listener.onCategoriesLoaded(categories);
            }

            @Override
            public void onError(String errorMessage) {
                categories.clear();
                categories.addAll(repository.getSampleCategories());
                listener.onCategoriesLoaded(categories);
            }
        });
    }

    public List<Category> getCachedCategories() {
        return new ArrayList<>(categories);
    }

    public Category getCategoryById(int id) {
        for (Category category : categories) {
            if (category.getId() == id) {
                return category;
            }
        }
        return null;
    }

    public Category getCategoryBySlug(String slug) {
        for (Category category : categories) {
            if (category.getSlug().equals(slug)) {
                return category;
            }
        }
        return null;
    }

    public List<String> getCategoryNames() {
        List<String> names = new ArrayList<>();
        names.add("Semua"); 
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;
    }
}