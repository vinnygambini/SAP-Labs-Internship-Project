package test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class ReservationServlet
 */
public class ReservationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	public static UserDAO usrDAO;
	public static TourDAO tourDAO;
	public static ReservationDAO reservationDAO;
	private DataSource ds = null;
	
	private Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
	
	@Override
	public void init() throws ServletException {
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx
					.lookup("java:comp/env/jdbc/DefaultDB");
		} catch (Exception e) {
			logger.error("Exception in init", e);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String tourID = request.getParameter("fieldTourId");
		User usr = (User) request.getSession().getAttribute("user");
		String eMail = usr.geteMail();
		
		Reservation reserv = new Reservation(eMail, tourID, 1);
		
		try {
			if(usr.getRole().equals("user")){
				reservationDAO.addReservation(reserv);
				tourDAO.decrementCapacity(tourID);
			}else{
				if(usr.getRole().equals("admin")){
					tourDAO.cancelTour(tourID);
				}
			}
			ArrayList<Reservation> userReservationsList = reservationDAO.getUserReservations(usr);
			request.getSession(true).setAttribute("userReservationsList", userReservationsList);
			request.getServletContext().getRequestDispatcher("/destinations.jsp").forward(request, response);
			
		} catch (SQLException e) {
			response.getWriter().println(e);
			e.printStackTrace();
		}		
		
		
		
		
	}

}
