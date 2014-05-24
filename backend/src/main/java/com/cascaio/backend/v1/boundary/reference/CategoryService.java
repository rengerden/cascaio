package com.cascaio.backend.v1.boundary.reference;

import com.cascaio.api.v1.reference.CategoryCreateRequest;
import com.cascaio.api.v1.reference.CategoryResponse;
import com.cascaio.api.v1.reference.CategoryUpdateRequest;
import com.cascaio.backend.v1.boundary.BaseService;
import com.cascaio.backend.v1.entity.reference.Category;
import com.cascaio.backend.v1.entity.reference.adapter.CategoryAdapter;
import org.jboss.ejb3.annotation.SecurityDomain;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Path;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
@Path("/reference/categories")
@Stateless
@SecurityDomain("keycloak")
public class CategoryService extends BaseService<
        CategoryCreateRequest,
        CategoryUpdateRequest,
        CategoryResponse,
        Category,
        CategoryAdapter> {

    @Inject
    CategoryAdapter adapter;

    @Override
    public CategoryAdapter getAdapter() {
        return adapter;
    }

    @Override
    public Class<Category> getPersistentClass() {
        return Category.class;
    }
}
