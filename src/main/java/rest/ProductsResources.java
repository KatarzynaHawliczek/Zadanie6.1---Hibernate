package rest;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import domain.Comment;
import domain.Product;

@Path("/products")
@Stateless
public class ProductsResources
{
	
	@PersistenceContext
	EntityManager em;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getAllProducts()
	{
		return em.createNamedQuery("product.all", Product.class).getResultList();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addProduct(Product product)
	{
		em.persist(product);
		em.flush();
		return Response.ok(product.getId()).build();
	}
	
	@GET
	@Path("/{productId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProductById(@PathParam("productId") int productId)
	{
		Product result = em.createNamedQuery("product.id", Product.class)
				   .setParameter("productId", productId)
				   .getSingleResult();
		if(result==null)
		{
			return Response.status(404).build();
		}
		return Response.ok(result).build();
	}
	
	@PUT
	@Path("/{productId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateProduct(@PathParam("productId") int productId, Product p)
	{
		Product result = em.createNamedQuery("product.id", Product.class)
				   .setParameter("productId", productId)
				   .getSingleResult();
		if(result==null)
		{
			return Response.status(404).build();
		}
		result.setName(p.getName());
		result.setPrice(p.getPrice());
		result.setCategory(p.getCategory());
		em.persist(result);
		return Response.ok().build();
	}
	
	@GET
	@Path("/byName/{productName}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProductByName(@PathParam("productName") String productName)
	{
		return em.createNamedQuery("product.name", Product.class)
					     .setParameter("productName", productName)
					     .getResultList();
	}
	
	@GET
	@Path("/byCategory/{productCategory}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProductByCategory(@PathParam("productCategory") String productCategory)
	{
		return em.createNamedQuery("product.category", Product.class)
					     .setParameter("productCategory", productCategory)
					     .getResultList();
	}
	
	@GET
	@Path("/byPrice/{min}/{max}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Product> getProductByPrice(@PathParam("min") int min, @PathParam("max") int max)
	{
		return em.createNamedQuery("product.price", Product.class)
				         .setParameter("min", min)
				         .setParameter("max", max)
				         .getResultList();
	}
	
	@GET
	@Path("/{productId}/comments")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Comment> getComments(@PathParam("productId") int productId)
	{
		Product result = em.createNamedQuery("product.id", Product.class)
				   .setParameter("productId", productId)
				   .getSingleResult();
		if(result==null)
		{
			return null;
		}
		return result.getComments();
	}
	
	@POST
	@Path("/{productId}/comments")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addComment(@PathParam("productId") int productId, Comment comment)
	{
		Product result = em.createNamedQuery("product.id", Product.class)
				   .setParameter("productId", productId)
				   .getSingleResult();
		if(result==null)
		{
			return Response.status(404).build();
		}
		comment.setProduct(result);
		result.getComments().add(comment);
		em.persist(comment);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/{productId}/comments/{commentId}")
	public Response deleteComment(@PathParam("productId") int productId, @PathParam("commentId") int commentId)
	{
		Product result = em.createNamedQuery("product.id", Product.class)
				   .setParameter("productId", productId)
				   .getSingleResult();
		if(result==null)
		{
			return Response.status(404).build();
		}
		int i=0;
		for(Comment comment : result.getComments())
		{
			if(comment.getId()==commentId)
			{
				result.getComments().remove(i);
				return Response.ok().build();
			}
			i++;
		}
		return Response.status(404).build();
	}
	
}
