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
package com.cascaio.backend.v1.boundary;

import com.cascaio.api.v1.BaseUpdateRequest;
import com.cascaio.api.v1.ReadRequestById;
import com.cascaio.backend.v1.entity.CascaioEntity;
import com.cascaio.backend.v1.entity.CascaioEntity_;
import com.cascaio.backend.v1.entity.EntityAdapter;
import org.slf4j.Logger;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author <a href="mailto:juraci.javadoc@kroehling.de">Juraci Paixão Kröhling</a>
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RolesAllowed({"admin"})
public abstract class BaseService<
        CreateRequest,
        UpdateRequest extends BaseUpdateRequest,
        QueryRequest,
        ReadRequest extends ReadRequestById,
        ApiResponse,
        Persistent extends CascaioEntity,
        Adapter extends EntityAdapter<CreateRequest, UpdateRequest, ApiResponse, Persistent>
        > {

    @Inject
    EntityManager entityManager;

    @Inject
    Adapter adapter;

    @Inject
    Persistent persistentSample;

    @Inject
    Logger logger;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAsJson(@Valid CreateRequest request) {
        return wrapInResponse(create(request));
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAsFormParameters(@Valid @BeanParam CreateRequest request) {
        return wrapInResponse(create(request));
    }

    public ApiResponse create(CreateRequest request) {
        preCreate(request);
        Persistent persistent = getInstrumentedAdapter().adaptCreate(request);
        prePersist(persistent);
        createAsEntity(persistent);
        ApiResponse response = getInstrumentedAdapter().adaptPersistent(persistent);
        postCreate(response);
        return response;
    }

    public Persistent createAsEntity(Persistent persistent) {
        getEntityManager().persist(persistent);
        return persistent;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAsJson(@Valid UpdateRequest request) {
        return wrapInResponse(update(request));
    }

    @PUT
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAsFormParameters(@Valid @BeanParam UpdateRequest request) {
        return wrapInResponse(update(request));
    }

    public ApiResponse update(UpdateRequest request) {
        preUpdate(request);
        Persistent persistent = getInstrumentedAdapter().adaptUpdate(request, readAsEntity(request.getId()));
        getEntityManager().persist(persistent);
        ApiResponse response = getInstrumentedAdapter().adaptPersistent(persistent);
        postUpdate(response);
        return response;
    }

    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    public Response read(@Valid @BeanParam ReadRequest request) {
        preRead(request);
        ApiResponse response = getInstrumentedAdapter().adaptPersistent(readAsEntity(request.getId()));
        postRead(response);
        return wrapInResponse(response);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    public Response list(@Valid @BeanParam QueryRequest request) {
        List<ApiResponse> response = getInstrumentedAdapter().adaptPersistent(listAsEntity(request));
        postList(request, response);
        return wrapInResponse(response);
    }

    @RolesAllowed({"user", "admin"})
    public List<Persistent> listAsEntity(QueryRequest request) {
        preList(request);
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Persistent> query = builder.createQuery(getPersistentClass());

        Root<Persistent> root = query.from(getPersistentClass());
        query.select(root);
        instrumentQuery(builder, root, query, request);

        List<Persistent> response = getEntityManager().createQuery(query).getResultList();
        postListAsEntity(response);
        return response;
    }

    @DELETE
    @Path("{id}")
    public Response delete(@Valid @BeanParam ReadRequest request) {
        preDelete(request);
        Persistent persistent = readAsEntity(request.getId());
        getEntityManager().remove(persistent);
        return Response.ok().build();
    }

    @RolesAllowed({"user", "admin"})
    public Persistent readAsEntity(String id) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Persistent> query = builder.createQuery(getPersistentClass());
        Root<Persistent> root = query.from(getPersistentClass());
        query.select(root);
        query.where(builder.equal(root.get(CascaioEntity_.id), id));
        instrumentQuery(builder, root, query, id);
        return getEntityManager().createQuery(query).getSingleResult();
    }

    public void preCreate(CreateRequest request) {
    }

    public void postCreate(ApiResponse response) {
    }

    @RolesAllowed({"user", "admin"})
    public void preRead(ReadRequest request) {
    }

    @RolesAllowed({"user", "admin"})
    public void postRead(ApiResponse response) {
    }

    public void preUpdate(UpdateRequest request) {
    }

    public void postUpdate(ApiResponse response) {
    }

    public void preDelete(ReadRequest request) {
    }

    @RolesAllowed({"user", "admin"})
    public void preList(QueryRequest request) {
    }

    public void prePersist(Persistent persistent) {
    }

    @RolesAllowed({"user", "admin"})
    public void postList(QueryRequest request, List<ApiResponse> response) {
    }

    @RolesAllowed({"user", "admin"})
    public void postListAsEntity(List<Persistent> response) {
    }

    public Adapter instrumentAdapter(Adapter adapter) {
        return adapter;
    }

    public Adapter getAdapter() {
        return this.adapter;
    }

    public void instrumentQuery(CriteriaBuilder builder, Root<Persistent> root, CriteriaQuery<Persistent> query, String id) {
    }

    public void instrumentQuery(CriteriaBuilder builder, Root<Persistent> root, CriteriaQuery<Persistent> query, QueryRequest request) {
    }

    public Response wrapInResponse(ApiResponse response) {
        return Response.ok().entity(response).build();
    }

    public Response wrapInResponse(List<ApiResponse> response) {
        return Response.ok().entity(response).build();
    }

    public ApiResponse unwrapResponse(Response response) {
        return (ApiResponse) response.getEntity();
    }

    public List<ApiResponse> unwrapResponseList(Response response) {
        return (List<ApiResponse>) response.getEntity();
    }

    private Adapter getInstrumentedAdapter() {
        Adapter adapter = getAdapter();
        return instrumentAdapter(adapter);
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    private Class<Persistent> getPersistentClass() {
        return (Class<Persistent>) persistentSample.getClass();
    }
}
