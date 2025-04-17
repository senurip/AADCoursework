package com.example.CeylonEE.service.impl;

import com.example.CeylonEE.dto.BookingDTO;
import com.example.CeylonEE.dto.PackageDTO;
import com.example.CeylonEE.dto.Response;
import com.example.CeylonEE.entity.Booking;
import com.example.CeylonEE.entity.Package;
import com.example.CeylonEE.entity.User;
import com.example.CeylonEE.exception.OurException;
import com.example.CeylonEE.repo.BookingRepo;
import com.example.CeylonEE.repo.PackageRepo;
import com.example.CeylonEE.repo.UserRepo;
import com.example.CeylonEE.service.interfac.IBookingService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class BookingService implements IBookingService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PackageRepo packageRepo;

    @Autowired
    private BookingRepo bookingRepo;


    @Override
    public Response getAllBookings() {
        Response response = new Response();
        try {
            List<Booking> bookings = bookingRepo.findAll(Sort.by(Sort.Direction.DESC, "bookingId"));
            List<BookingDTO> bookingDTOs = new ArrayList<>(); // Use ArrayList for more control

            for (Booking booking : bookings) {
                BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class); // Map the basic booking info
                if (booking.getPkg() != null) {
                    PackageDTO packageDTO = modelMapper.map(booking.getPkg(), PackageDTO.class); // Map the package
                    bookingDTO.setPackageDTO(packageDTO);  //  SET the packageDTO into bookingDTO
                }
                bookingDTOs.add(bookingDTO);
            }

            response.setStatusCode(200);
            response.setMessage("Bookings found");
            response.setBookingList(bookingDTOs);

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error " + e.getMessage());
        }
        return response;
    }

    @Override
    public Response getAllBookingsByUserId(Long userId) {
        Response response = new Response();
        try {
            User user = userRepo.findById(userId).orElseThrow(() -> new OurException("User Not Found"));

            List<Booking> bookingList = user.getBookings();

            List<BookingDTO> bookingDTOList = modelMapper.map(bookingList, new TypeToken<List<BookingDTO>>() {
            }.getType());
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setBookingList(bookingDTOList);


        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error " + e.getMessage());
        }
        return response;
    }

    @Override
    public boolean existsByCustomerIdAndPackageId(Long customerId, Long packageId) {

        return bookingRepo.existsByCustomerIdAndPkgId(customerId, packageId);
    }

    @Override
    public int countByCustomerIdAndPkgId(Long customerId, Long packageId) {

        return bookingRepo.countByCustomerIdAndPkgId(customerId, packageId);
    }

    @Override
    public Response getPackagesWithPurchaseCounts(Long userId) {
        Response response = new Response();
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new OurException("User not found"));

            // Get all bookings for the user.
            List<Booking> bookings = bookingRepo.findAllByCustomer(user);

            // Group bookings by package and count occurrences.
            Map<Package, Long> packageCounts = bookings.stream()
                    .collect(Collectors.groupingBy(Booking::getPkg, Collectors.counting()));

            // Convert to a list of PackageDTOs with purchase counts.
            List<PackageDTO> packageDTOs = new ArrayList<>();
            for (Map.Entry<Package, Long> entry : packageCounts.entrySet()) {
                Package pkg = entry.getKey();
                Long count = entry.getValue();
                PackageDTO dto = modelMapper.map(pkg, PackageDTO.class);
                dto.setPurchaseCount(count.intValue());
                packageDTOs.add(dto);
            }

            response.setStatusCode(200);
            response.setMessage("Packages with purchase counts retrieved successfully");
            response.setPackageList(packageDTOs);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error retrieving packages: " + e.getMessage());
        }
        return response;
    }
}
