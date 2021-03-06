package cn.edu.sdut.softlab.controller;

import cn.edu.sdut.softlab.converterandvalidator.IllegalValidator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import cn.edu.sdut.softlab.model.Category;
import cn.edu.sdut.softlab.model.Item;
import cn.edu.sdut.softlab.service.CategoryFacade;
import cn.edu.sdut.softlab.service.ItemFacade;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

@Named("itemaddmanager")
@RequestScoped
public class ItemAddManager extends IllegalValidator{

    @Inject
    private transient Logger logger;

    @Inject
    EntityManager em;

    @Inject
    UserTransaction utx;

    @Inject
    ItemFacade itemService;

    @Inject
    CategoryFacade categoryservice;

    private String categoryname;

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    private Item newItem = new Item();

    public Item getNewItem() {
        if (newItem.getCategory() == null) {
            newItem.setCategory(new Category());
        }
        return newItem;
    }

    public void setNewItem(Item newItem) {
        this.newItem = newItem;
    }

    public List<Category> AllCategory() throws Exception {
        return categoryservice.findAll(Category.class);
    }

    public Category findCategoryByName(String name) {
        Category category = new Category();
        category = null;
        List<Category> categories = categoryservice.findAllCategory();
        Iterator it = categories.iterator();
        while (it.hasNext()) {
            Category c = (Category) it.next();
            if (c.getName().equals(name)) {
                category = c;
            }
        }
        return category;
    }

    /**
     * 向数据库中添加前台页面输入的Item对象
     *
     * @return 重定向到当前页面（刷新页面）
     * @throws Exception
     */
    public String addItem() throws Exception {
        if (!newItem.getName().equals("")) {
            try {
                utx.begin();
                itemService.create(newItem);
                logger.log(Level.INFO, "Added {0}", newItem);
                return null;
            }
            finally {
                utx.commit();
            }
        } else {
            return "/Error.xhtml?faces-redirect=true";
        }
    }
    
    public void ItemAddValidator(FacesContext fc, UIComponent component, Object value) {
        AddValidator(value);
        List<Item> itemList = itemService.findAll(Item.class);
        for (Item i : itemList) {
            if (((String) value).equals(i.getName())) {
                throw new ValidatorException(new FacesMessage("您要添加的物品已有，请验证确定后再次添加！"));
            }
        }
    }

}
