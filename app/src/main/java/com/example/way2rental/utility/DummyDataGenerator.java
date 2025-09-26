package com.example.way2rental.utility; // Or any package you prefer

import com.example.way2rental.model.*; // Import all your model classes

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DummyDataGenerator {

    private static final Random random = new Random();
    private static final AtomicInteger productGlobalId = new AtomicInteger(1);

    // --- Configuration & Sample Data ---
    private static final List<String> CATEGORY_KEYS = Arrays.asList(
            "House", "Apartment", "1BHK", "2BHK", "3BHK", "1RK", "SR"
    );

    // Mapping for product types based on category key
    private static final Map<String, String> CATEGORY_TO_PRODUCT_TYPE_MAP = new HashMap<>();
    static {
        CATEGORY_TO_PRODUCT_TYPE_MAP.put("House", "HOUSE");
        CATEGORY_TO_PRODUCT_TYPE_MAP.put("Apartment", "APARTMENT");
        CATEGORY_TO_PRODUCT_TYPE_MAP.put("1BHK", "ONE_BHK_FLAT"); // Example: more specific type
        CATEGORY_TO_PRODUCT_TYPE_MAP.put("2BHK", "TWO_BHK_FLAT");
        CATEGORY_TO_PRODUCT_TYPE_MAP.put("3BHK", "THREE_BHK_FLAT");
        CATEGORY_TO_PRODUCT_TYPE_MAP.put("1RK", "ONE_RK_STUDIO");
        CATEGORY_TO_PRODUCT_TYPE_MAP.put("SR", "SINGLE_ROOM_UNIT");
    }

    private static final Map<String, Map<String, Object>> CATEGORY_ATTRIBUTES_CONFIG = new HashMap<>();
    static {
        Map<String, Object> oneBhkAttr = new HashMap<>();
        oneBhkAttr.put("bedrooms", 1);
        oneBhkAttr.put("bathrooms", Collections.singletonList(1));
        CATEGORY_ATTRIBUTES_CONFIG.put("1BHK", oneBhkAttr);

        Map<String, Object> twoBhkAttr = new HashMap<>();
        twoBhkAttr.put("bedrooms", 2);
        twoBhkAttr.put("bathrooms", Arrays.asList(1, 2));
        CATEGORY_ATTRIBUTES_CONFIG.put("2BHK", twoBhkAttr);

        Map<String, Object> threeBhkAttr = new HashMap<>();
        threeBhkAttr.put("bedrooms", 3);
        threeBhkAttr.put("bathrooms", Arrays.asList(2, 3));
        CATEGORY_ATTRIBUTES_CONFIG.put("3BHK", threeBhkAttr);

        Map<String, Object> oneRkAttr = new HashMap<>();
        oneRkAttr.put("bedrooms", 1); // Often 1 room is bedroom/hall
        oneRkAttr.put("bathrooms", Collections.singletonList(1));
        CATEGORY_ATTRIBUTES_CONFIG.put("1RK", oneRkAttr);

        Map<String, Object> srAttr = new HashMap<>();
        srAttr.put("bedrooms", 1);
        srAttr.put("bathrooms", Collections.singletonList(1));
        CATEGORY_ATTRIBUTES_CONFIG.put("SR", srAttr);

        Map<String, Object> houseAttr = new HashMap<>();
        houseAttr.put("bedrooms", Arrays.asList(2, 3, 4, 5)); // Options for random choice
        houseAttr.put("bathrooms", Arrays.asList(1, 2, 3, 4));
        CATEGORY_ATTRIBUTES_CONFIG.put("House", houseAttr);

        Map<String, Object> aptAttr = new HashMap<>();
        aptAttr.put("bedrooms", Arrays.asList(1, 2, 3));
        aptAttr.put("bathrooms", Arrays.asList(1, 2));
        CATEGORY_ATTRIBUTES_CONFIG.put("Apartment", aptAttr);
    }

    private static final List<String> SAMPLE_ADJECTIVES = Arrays.asList(
            "Spacious", "Modern", "Cozy", "Luxury", "Affordable", "Bright", "Charming",
            "Newly Renovated", "Well-Maintained", "Budget-Friendly", "Elegant", "Urban"
    );
    private static final List<String> SAMPLE_PROPERTY_NOUNS = Arrays.asList(
            "Flat", "Home", "Unit", "Place", "Condo", "Studio", "Residence", "Property", "Haven", "Retreat"
    );
    private static final List<String> SAMPLE_AREAS = Arrays.asList(
            "Vijay Nagar", "Palasia", "Rau", "Mahalakshmi Nagar", "Bengali Square",
            "Annapurna Road", "Scheme No. 78", "Tilak Nagar", "Geeta Bhawan", "AB Road"
    );
    private static final List<String> SAMPLE_CITIES = Arrays.asList("Indore", "Bhopal", "Ujjain", "Dewas", "Ratlam");
    private static final List<String> SAMPLE_STATES = Collections.singletonList("Madhya Pradesh");
    private static final String SAMPLE_COUNTRY = "India";

    private static final List<String> SAMPLE_FACILITIES = Arrays.asList(
            "Parking", "Lift", "Power Backup", "Security Guard", "WiFi", "Fully Furnished",
            "Semi-Furnished", "Air Conditioning", "Geyser", "Modular Kitchen", "Gymnasium",
            "Swimming Pool", "Clubhouse", "CCTV Surveillance", "Intercom", "Water Purifier",
            "Piped Gas", "Balcony", "Garden Access", "Servant Quarters"
    );
    private static final List<String> SAMPLE_TAGS = Arrays.asList(
            "family-friendly", "bachelor-friendly", "pet-friendly", "student-friendly", "near-market",
            "quiet-area", "good-for-wfh", "low-deposit", "immediate-possession", "main-road-facing",
            "vastu-compliant", "gated-society"
    );
    private static final List<String> SAMPLE_RULES = Arrays.asList(
            "no_pets_allowed", "no_smoking_inside", "no_loud_parties_after_10pm",
            "bachelors_preferred", "families_only", "visitors_parking_separate", "rent_includes_maintenance"
    );
    private static final List<String> SAMPLE_NEARBY_PLACE_KEYS = Arrays.asList(
            "Supermarket", "Hospital", "School", "Metro Station", "Bus Stop",
            "Shopping Mall", "Public Park", "Restaurant", "ATM", "Pharmacy"
    );
    private static final List<String> PRODUCT_STATUS_OPTIONS = Arrays.asList(
            "AVAILABLE", "RENTED", "UNDER_MAINTENANCE", "BOOKED", "COMING_SOON"
    );
    private static final List<String> PRICE_UNIT_OPTIONS = Arrays.asList(
            "PER_MONTH", "PER_DAY", "PER_YEAR" // For pricing.basePrice
    );
    private static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter ISO_OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


    // --- Helper Functions ---

    private static String getRandomDate(int startYear, int endYear) {
        long minDay = LocalDate.of(startYear, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(endYear, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay).format(ISO_DATE_FORMATTER);
    }

    private static String getRandomTimestamp() {
        long currentMillis = System.currentTimeMillis();
        long randomMillisAgo = ThreadLocalRandom.current().nextLong(0, 365L * 24 * 60 * 60 * 1000); // Up to 1 year ago
        return OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(currentMillis - randomMillisAgo), ZoneOffset.UTC)
                .format(ISO_OFFSET_DATE_TIME_FORMATTER);
    }

    private static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(random.nextInt(list.size()));
    }

    private static <T> List<T> getRandomSublist(List<T> list, int count) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        List<T> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled, random);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }

    private static String generateProductName(String categoryKey, String area, String city) {
        String adj = getRandomElement(SAMPLE_ADJECTIVES);
        String noun = getRandomElement(SAMPLE_PROPERTY_NOUNS);
        String typeDesc = categoryKey; // Use the category key itself for description

        if (categoryKey.equals("SR")) typeDesc = "Single Room"; // Friendlier name for SR

        return String.format("%s %s %s in %s, %s", adj, typeDesc, noun, area, city);
    }


    // --- Main Product Generation Method ---
    public static Map<String, List<Product>> generateProductsByCategory(int totalProductsToGenerate) {
        Map<String, List<Product>> productsByCategory = new HashMap<>();
        for (String key : CATEGORY_KEYS) {
            productsByCategory.put(key, new ArrayList<>());
        }

        for (int i = 0; i < totalProductsToGenerate; i++) {
            String assignedCategoryKey = getRandomElement(CATEGORY_KEYS); // Randomly assign to a top-level category name
            if (assignedCategoryKey == null) continue;

            Product product = new Product();
            int currentId = productGlobalId.getAndIncrement();
            product.setId(currentId);

            String selectedArea = getRandomElement(SAMPLE_AREAS);
            String selectedCity = getRandomElement(SAMPLE_CITIES);

            product.setName(generateProductName(assignedCategoryKey, selectedArea, selectedCity));
            product.setType(CATEGORY_TO_PRODUCT_TYPE_MAP.getOrDefault(assignedCategoryKey, "APARTMENT")); // Map to defined product type
            product.setStatus(getRandomElement(PRODUCT_STATUS_OPTIONS));
            product.setDescription(String.format("Discover this wonderful %s. %s It offers a great living experience with modern amenities and a convenient location. %s",
                    product.getName(),
                    getRandomElement(Arrays.asList("A perfect blend of comfort and style.", "Designed for modern living.", "Your ideal new home awaits.")),
                    getRandomElement(Arrays.asList("Perfect for families.", "Ideal for bachelors and working professionals.", "A great choice for students or singles."))
            ));

            // Pricing
            Pricing pricing = new Pricing();
            pricing.setBasePrice(Math.round(random.nextDouble() * (50000 - 5000) + 5000)); // 5k to 50k
            pricing.setSecurityDeposit(Math.round(pricing.getBasePrice() * (random.nextDouble() * (2.5 - 1.0) + 1.0))); // 1x to 2.5x of base
            pricing.setPriceUnit(getRandomElement(PRICE_UNIT_OPTIONS));
            pricing.setNegotiable(random.nextBoolean());
            pricing.setCleaningFee(random.nextBoolean() ? Math.round(random.nextDouble() * 2000) : 0);
            pricing.setMaintenanceFee(random.nextBoolean() ? Math.round(random.nextDouble() * (3000-200)+200) : 0);
            pricing.setDiscountPercent(random.nextDouble() < 0.2 ? random.nextInt(21) : 0); // 20% chance of discount up to 20%
            product.setPricing(pricing);

            // ProductLocation
            ProductLocation pLocation = new ProductLocation();
            pLocation.setAddressLine1(String.format("%d, %s, %s", random.nextInt(200) + 1, getRandomElement(Arrays.asList("Main Street", "Cross Road", "Avenue", "Lane")), selectedArea));
            pLocation.setCity(selectedCity);
            pLocation.setState(getRandomElement(SAMPLE_STATES));
            pLocation.setCountry(SAMPLE_COUNTRY);
            pLocation.setZipCode(String.valueOf(random.nextInt(10000) + 450000)); // Example pincodes
            Coordinates coords = new Coordinates();
            coords.setLat(22.6 + random.nextDouble() * 0.2); // Approx Indore Lat
            coords.setLng(75.7 + random.nextDouble() * 0.2); // Approx Indore Lng
            pLocation.setCoordinates(coords);
            product.setProductLocation(pLocation);

            // Owner
            Owner owner = new Owner();
            owner.setId(random.nextInt(1000) + 1);
            owner.setContactNumber("+919" + String.format("%09d", random.nextInt(1_000_000_000)));
            owner.setVerified(random.nextBoolean());
            product.setOwner(owner);

            // Media
            Media media = new Media();
            List<ImageItem> images = new ArrayList<>();
            int numImages = random.nextInt(3) + 1; // 1 to 3 images
            for (int j = 0; j < numImages; j++) {
                ImageItem img = new ImageItem();
                // Using picsum.photos for varied placeholder images based on product ID and image index
                img.setUrl(String.format("https://picsum.photos/seed/%d-%d/800/600", currentId, j));
                img.setPrimary(j == 0); // First image is primary
                images.add(img);
            }
            media.setImages(images);
            media.setVideos(random.nextBoolean() ? Collections.singletonList("https://www.example.com/sample_video.mp4") : Collections.emptyList());
            product.setMedia(media);

            // Meta
            Meta meta = new Meta();
            Attributes attributes = new Attributes();
            Map<String, Object> attrConfig = CATEGORY_ATTRIBUTES_CONFIG.get(assignedCategoryKey);
            if (attrConfig != null) {
                if (attrConfig.containsKey("bedrooms") && attrConfig.get("bedrooms") instanceof Integer) {
                    attributes.setBedrooms((Integer) attrConfig.get("bedrooms"));
                } else if (attrConfig.containsKey("bedrooms") && attrConfig.get("bedrooms") instanceof List) {
                    attributes.setBedrooms((Integer) getRandomElement((List<Integer>) attrConfig.get("bedrooms")));
                } else {
                     attributes.setBedrooms(random.nextInt(3)+1); // Default 1-3
                }

                if (attrConfig.containsKey("bathrooms") && attrConfig.get("bathrooms") instanceof List) {
                     attributes.setBathrooms((Integer) getRandomElement((List<Integer>) attrConfig.get("bathrooms")));
                } else {
                     attributes.setBathrooms(random.nextInt(2)+1); // Default 1-2
                }
            } else {
 // Default if category not in specific config (should not happen with current setup)
                 attributes.setBedrooms(random.nextInt(3)+1);
                 attributes.setBathrooms(random.nextInt(2)+1);
            }
            attributes.setFloor(random.nextInt(15) + 1); // 1 to 15 floors
            meta.setAttributes(attributes);

            meta.setTags(getRandomSublist(SAMPLE_TAGS, random.nextInt(4) + 2)); // 2 to 5 tags
            meta.setFacilities(getRandomSublist(SAMPLE_FACILITIES, random.nextInt(6) + 3)); // 3 to 8 facilities
            meta.setRules(getRandomSublist(SAMPLE_RULES, random.nextInt(3) + 1)); // 1 to 3 rules

            Map<String, String> nearbyPlaces = new HashMap<>();
            List<String> nearbyKeys = getRandomSublist(SAMPLE_NEARBY_PLACE_KEYS, random.nextInt(3) + 2); // 2-4 nearby places
            for (String key : nearbyKeys) {
                nearbyPlaces.put(key, String.format("%dm", (random.nextInt(10) + 1) * 100)); // 100m to 1km
            }
            meta.setNearby(nearbyPlaces);
            product.setMeta(meta);

            // ReviewsSummary (Simplified)
            ReviewsSummary reviewsSummary = new ReviewsSummary();
            reviewsSummary.setAvgRating(Math.round((random.nextDouble() * 2.0 + 3.0) * 10.0) / 10.0); // 3.0 to 5.0 rating
            reviewsSummary.setTotalReviews(random.nextInt(50));
            reviewsSummary.setList(Collections.emptyList()); // Can be expanded to add dummy ReviewItem
            product.setReviews(reviewsSummary);

            // Availability
            Availability availability = new Availability();
            availability.setFrom(getRandomDate(2024, 2024));
            availability.setTo(getRandomDate(2025, 2026));
            product.setAvailability(availability);

            // Seo (Simplified)
            Seo seo = new Seo();
            seo.setTitle(product.getName() + " for Rent");
            seo.setDescription("Find the best " + product.getName() + ". " + product.getDescription().substring(0, Math.min(product.getDescription().length(), 100)) + "...");
            seo.setSlug(product.getName().toLowerCase().replace(" ", "-").replaceAll("[^a-z0-9-]", "") + "-" + currentId);
            seo.setLanguage("en");
            product.setSeo(seo);

            product.setFeatured(random.nextDouble() < 0.15); // 15% chance of being featured

            // Timestamps
            Timestamps timestamps = new Timestamps();
            timestamps.setCreatedAt(getRandomTimestamp());
            timestamps.setUpdatedAt(getRandomTimestamp());
            product.setTimestamps(timestamps);

            productsByCategory.get(assignedCategoryKey).add(product);
        }
        return productsByCategory;
    }

    // Example of how to use this generator and print JSON (using Gson, which you'd have in your Android project)

    public static void main(String[] args) {
        Map<String, List<Product>> generatedData = generateProductsByCategory(1000);

        // --- You would typically use Gson in your Android project ---
        // com.google.gson.Gson gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        // String jsonOutput = gson.toJson(generatedData);
        // System.out.println(jsonOutput);
        // --- End Gson example ---

        // Simple print for verification without Gson in this standalone example
        generatedData.forEach((category, productList) -> {
            // Write Generated data into files.
            System.out.println("Category: " + category + " (" + productList.size() + " items)");
            // productList.forEach(p -> System.out.println("  - " + p.getName())); // Simple check
        });
         System.out.println("Total products generated: " + (productGlobalId.get() -1));
    }
}
