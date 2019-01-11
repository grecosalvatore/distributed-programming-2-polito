package it.polito.dp2.RNS.sol3.service.resources;


import it.polito.dp2.RNS.sol3.jaxb.*;
import it.polito.dp2.RNS.sol3.service.RnsService.RnsService;

import java.net.URI;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.datatype.XMLGregorianCalendar;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/rns")
@Api(value = "/rns")
public class RnsResources {
	public UriInfo uriInfo;
	
	RnsService service = new RnsService();

	public RnsResources(@Context UriInfo uriInfo) {
		this.uriInfo = uriInfo;
	}
	
	@GET
    @ApiOperation(value = "getRns", notes = "reads main resource"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Rns getRns() {
		return service.getRns(uriInfo);
	}
	
	@GET
	@Path("/vehicles")
    @ApiOperation(value = "getVehicles", notes = "Read the set of all vehicles in the system")
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Vehicles getVehicles() {
		return service.getvehicles(uriInfo);
	}

	@GET
	@Path("/vehicles/{id}")
    @ApiOperation(value = "getVehicle", notes = "Read single vehicle"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Vehicle getVehicle(	) {
		return null;
	}
	
	@PUT
	@Path("/vehicles/{id}")
    @ApiOperation(value = "putVehicle", notes = "Enter a vehicle in the system"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response putVehicle(	) {
		return null;
	}
	
	@GET
	@Path("/vehicles/{id}/state")
    @ApiOperation(value = "getState", notes = "Read the current state for the vehicle"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public State getState() {
		return null;
	}
	
	@PUT
	@Path("/vehicles/{id}/state")
    @ApiOperation(value = "changeState", notes = "The vehicle change the current state"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void changeState() {
		return ;
	}

	@GET
	@Path("/vehicles/{id}/suggestedPath")
    @ApiOperation(value = "getSuggestedPath", notes = "Read the suggested path for the vehicle"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public SuggestedPath getSuggestedPath() {
		return null;
	}

	@GET
	@Path("/vehicles/{id}/currentPosition")
    @ApiOperation(value = "getCurrentPosition", notes = "Read the current position for the vehicle"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not found"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Position getCurrentPosition() {
		return null;
	}
	
	@PUT
	@Path("/vehicles/{id}/currentPosition")
    @ApiOperation(value = "changeCurrentPosition", notes = "Change of the current position for the vehicle"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "Created"),
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public SuggestedPath changeCurrentPosition() {
		return null;
	}

	@GET
	@Path("/places")
    @ApiOperation(value = "getPlaces", notes = "Read the set of all places in the system"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Places getPlaces() {
		return service.getPlaces(uriInfo);
	}
	
	@GET
	@Path("/places/{id}")
    @ApiOperation(value = "getPlace", notes = "Read single place"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "NOT FOUND"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Place getPlace(@PathParam("id") String placeId) {
		Place place = service.getPlace(uriInfo,placeId);
		if (place==null)
			throw new NotFoundException();
		return place;
		}
	
	@GET
	@Path("/places/{id}/currentVehicles")
    @ApiOperation(value = "getCurrentVehicles", notes = "Read all vehicles inside the place"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "NOT FOUND"),
    		})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Place getCurrentVehicles() {
		return null;
	}
	
	@GET
	@Path("/places/{id}/connectedTo")
    @ApiOperation(value = "getNextPlaces", notes = "Read all places connected to this place"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "NOT FOUND"),
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public ConnectedTo getNextPlaces() {
		return null;
	}
	
	@GET
	@Path("/places/gates")
    @ApiOperation(value = "getGates", notes = "Read the set of all gates in the system"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),  		
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Places getGates() {
		return service.getGates(uriInfo);
	}
	
	@GET
	@Path("/places/roadSegments")
    @ApiOperation(value = "getRoadSegments", notes = "Read the set of all road segments in the system"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),  		
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Places getRoadSegments() {
		return service.getRoadSegments(uriInfo);
	}
	
	@GET
	@Path("/places/parkingAreas")
    @ApiOperation(value = "getParkingAreas", notes = "Read the set of all parking areas in the system"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),  		
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Places getParkingAreas() {
		return service.getParkingAreas(uriInfo);
	}
	
	@GET
	@Path("/connections")
    @ApiOperation(value = "getConnections", notes = "Read the set of all connections in the system"
	)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),  		
    		})
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Connections getConnections() {
		return service.getConnections(uriInfo);
	}
	
	
}
