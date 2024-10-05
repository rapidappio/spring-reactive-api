package com.example.spring_reactive_api;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

@Data
@Table
@AllArgsConstructor
@NoArgsConstructor
class Car {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String make;
	private String model;
	private Integer year;
	private String color;
}

interface CarRepository extends R2dbcRepository<Car, Long> {
	Flux<Car> findByMake(String make);
}

@Service
class CarService {
	private final CarRepository carRepository;

	public CarService(CarRepository carRepository) {
		this.carRepository = carRepository;
	}

	public Flux<Car> getAllCars() {
		return carRepository.findAll();
	}

	public Mono<Car> getCarById(Long id) {
		return carRepository.findById(id);
	}

	public Mono<Car> createCar(Car car) {
		return carRepository.save(car);
	}

	public Mono<Car> updateCar(Long id, Car car) {
		return carRepository.findById(id)
				.flatMap(existingCar -> {
					car.setId(id);
					return carRepository.save(car);
				});
	}

	public Mono<Void> deleteCar(Long id) {
		return carRepository.deleteById(id);
	}
}

@RestController
@RequestMapping("/api/cars")
class CarController {
	private final CarService carService;

	public CarController(CarService carService) {
		this.carService = carService;
	}

	@GetMapping
	public Flux<Car> getAllCars() {
		return carService.getAllCars();
	}

	@GetMapping("/{id}")
	public Mono<Car> getCarById(@PathVariable Long id) {
		return carService.getCarById(id);
	}

	@PostMapping
	public Mono<Car> createCar(@RequestBody Car car) {
		return carService.createCar(car);
	}

	@PutMapping("/{id}")
	public Mono<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
		return carService.updateCar(id, car);
	}

	@DeleteMapping("/{id}")
	public Mono<Void> deleteCar(@PathVariable Long id) {
		return carService.deleteCar(id);
	}
}
