package com.cts.turfservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cts.turfservice.client.OwnerServiceClient;
import com.cts.turfservice.dto.TurfDTO;
import com.cts.turfservice.entity.Turf;
import com.cts.turfservice.exception.TurfAlreadyExistsException;
import com.cts.turfservice.exception.TurfNotFoundException;
import com.cts.turfservice.repository.TurfRepository;
import com.cts.turfservice.service.TurfServiceImpl;

@ExtendWith(MockitoExtension.class)
class TurfServiceApplicationTests {

    @Mock
    private TurfRepository turfRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private OwnerServiceClient ownerServiceClient;

    @InjectMocks
    private TurfServiceImpl turfService;

    private Turf turf;
    private TurfDTO turfDTO;

    @BeforeEach
    public void setUp() {
        turf = new Turf();
        turf.setId(1L);
        turf.setName("Test Turf");
        turf.setLocation("Test Location");
        turf.setSportType("Football");
        turf.setPricePerHour(500);
        turf.setOwnerId(1L);
        turf.setDescription("Test Description");
        turf.setFacilities("Test Facilities");
        turf.setCapacity(10);
        turf.setOpeningTime(LocalTime.of(8, 0));
        turf.setClosingTime(LocalTime.of(22, 0));
        turf.setSurfaceType("Grass");

        turfDTO = new TurfDTO();
        turfDTO.setName("Test Turf");
        turfDTO.setLocation("Test Location");
        turfDTO.setSportType("Football");
        turfDTO.setPricePerHour(500);
        turfDTO.setDescription("Test Description");
        turfDTO.setFacilities("Test Facilities");
        turfDTO.setCapacity(10);
        turfDTO.setOpeningTime(LocalTime.of(8, 0));
        turfDTO.setClosingTime(LocalTime.of(22, 0));
        turfDTO.setSurfaceType("Grass");
    }

    @Test
    void testAddTurf() {
        when(turfRepository.existsByNameAndLocation(anyString(), anyString())).thenReturn(false);
        when(modelMapper.map(any(TurfDTO.class), any())).thenReturn(turf);
        when(turfRepository.save(any(Turf.class))).thenReturn(turf);

        Turf savedTurf = turfService.addTurf(1L, turfDTO);

        assertNotNull(savedTurf);
        assertEquals("Test Turf", savedTurf.getName());
        assertEquals("Test Location", savedTurf.getLocation());
        verify(ownerServiceClient, times(1)).updateOwnedTurfs(anyLong(), anyString());
    }

    @Test
    void testAddTurfTurfAlreadyExistsException() {
        when(turfRepository.existsByNameAndLocation(anyString(), anyString())).thenReturn(true);

        assertThrows(TurfAlreadyExistsException.class, () -> {
            turfService.addTurf(1L, turfDTO);
        });
    }

    @Test
    void testUpdateTurf() {
        when(turfRepository.findById(anyLong())).thenReturn(Optional.of(turf));
        doAnswer(invocation -> {
            TurfDTO src = invocation.getArgument(0);
            Turf dest = invocation.getArgument(1);
            dest.setName(src.getName());
            dest.setLocation(src.getLocation());
            dest.setSportType(src.getSportType());
            dest.setPricePerHour(src.getPricePerHour());
            dest.setDescription(src.getDescription());
            dest.setFacilities(src.getFacilities());
            dest.setCapacity(src.getCapacity());
            dest.setOpeningTime(src.getOpeningTime());
            dest.setClosingTime(src.getClosingTime());
            dest.setSurfaceType(src.getSurfaceType());
            return null;
        }).when(modelMapper).map(any(TurfDTO.class), any(Turf.class));
        when(turfRepository.save(any(Turf.class))).thenReturn(turf);

        Turf updatedTurf = turfService.updateTurf(1L, turfDTO);

        assertNotNull(updatedTurf);
        assertEquals("Test Turf", updatedTurf.getName());
        assertEquals("Test Location", updatedTurf.getLocation());
    }



    @Test
    void testUpdateTurfNotFoundException() {
        when(turfRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TurfNotFoundException.class, () -> {
            turfService.updateTurf(1L, turfDTO);
        });
    }

    @Test
    void testDeleteTurf() {
        when(turfRepository.findById(anyLong())).thenReturn(Optional.of(turf));
        doNothing().when(turfRepository).deleteById(anyLong());
        doNothing().when(ownerServiceClient).removeOwnedTurf(anyLong(), anyString());

        turfService.deleteTurf(1L);

        verify(turfRepository, times(1)).deleteById(anyLong());
        verify(ownerServiceClient, times(1)).removeOwnedTurf(anyLong(), anyString());
    }

    @Test
    void testDeleteTurfNotFoundException() {
        when(turfRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TurfNotFoundException.class, () -> {
            turfService.deleteTurf(1L);
        });
    }

    @Test
    void testGetTurfById() {
        when(turfRepository.findById(anyLong())).thenReturn(Optional.of(turf));

        Turf foundTurf = turfService.getTurfById(1L);

        assertNotNull(foundTurf);
        assertEquals("Test Turf", foundTurf.getName());
    }

    @Test
    void testGetTurfByIdNotFoundException() {
        when(turfRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TurfNotFoundException.class, () -> {
            turfService.getTurfById(1L);
        });
    }

    @Test
    void testGetAllTurfs() {
        when(turfRepository.findAll()).thenReturn(Arrays.asList(turf));

        List<Turf> turfs = turfService.getAllTurfs();

        assertNotNull(turfs);
        assertFalse(turfs.isEmpty());
    }

    @Test
    void testGetTurfsByOwnerId() {
        when(turfRepository.findByOwnerId(anyLong())).thenReturn(Arrays.asList(turf));

        List<Turf> turfs = turfService.getTurfsByOwnerId(1L);

        assertNotNull(turfs);
        assertFalse(turfs.isEmpty());
    }

//    @Test
//    void testSearchTurfs() {
//        when(turfRepository.findAll()).thenReturn(Arrays.asList(turf));
//
//        List<Turf> turfs = turfService.searchTurfs("Test Location", "Football", 400.0, 600.0);
//
//        assertNotNull(turfs);
//        assertFalse(turfs.isEmpty());
//    }
}
