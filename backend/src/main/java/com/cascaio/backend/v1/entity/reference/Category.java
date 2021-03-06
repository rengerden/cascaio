/* 
 * Copyright (C) 2014 Juraci Paixão Kröhling <juraci at kroehling.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cascaio.backend.v1.entity.reference;

import com.cascaio.backend.v1.entity.NamedCascaioEntity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
@Entity
public class Category extends NamedCascaioEntity {

    @OneToMany(mappedBy = "parent")
    @OrderBy(value = "name")
    private List<Category> subCategories = new ArrayList<>();

    @ManyToOne(optional = true)
    private Category parent;

    protected Category() {
        // to make JPA happy
    }

    public Category(String name) {
        super(name);
    }

    public Category(String id, String name) {
        super(id, name);
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        if (null != this.parent) this.parent.subCategories.remove(this);
        if (null != parent) parent.subCategories.add(this);

        this.parent = parent;
    }

    public void addSubCategory(Category subCategory) {
        subCategory.parent = this;
        this.subCategories.add(subCategory);
    }

    public List<Category> getSubCategories() {
        return Collections.unmodifiableList(subCategories);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + getId() +
                ",name=" + getName() +
                ",subCategories=" + subCategories +
                '}';
    }
}
