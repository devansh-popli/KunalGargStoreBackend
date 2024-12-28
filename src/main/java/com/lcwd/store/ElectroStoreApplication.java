package com.lcwd.store;

import com.lcwd.store.entities.Role;
import com.lcwd.store.entities.UOM;
import com.lcwd.store.repositories.RoleRepository;
import com.lcwd.store.repositories.UOMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.EmptyStackException;
import java.util.List;
import java.util.UUID;


@SpringBootApplication
@EnableTransactionManagement
public class ElectroStoreApplication implements CommandLineRunner {

    @Value("${admin.role.id}")
    private String role_admin_id;
    @Value("${normal.role.id}")
    private String role_normal_id;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UOMRepository uomRepository; // Your repository interface
    public static void main(String[] args) {

        SpringApplication.run(ElectroStoreApplication.class, args);
    }

    public void run(String... args) throws Exception {
        try {

            Role role_admin = Role.builder().roleId(role_admin_id).roleName("ROLE_ADMIN").build();
            Role role_normal = Role.builder().roleId(role_normal_id).roleName("ROLE_NORMAL").build();
            roleRepository.save(role_normal);
            roleRepository.save(role_admin);
            if (uomRepository.count() == 0) {
                // Data does not exist, insert it
                List<UOM> UOMList = createUOMData(); // Populate the list with your data
                uomRepository.saveAll(UOMList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private List<UOM> createUOMData() {
        List<UOM> UOMList = List.of(
                new UOM("1", "BAGS", "Measure", "BAG"),
                new UOM("2", "BALE", "Measure", "BAL"),
                new UOM("3", "BUNDLES", "Measure", "BDL"),
                new UOM("4", "BUCKLES", "Measure", "BKL"),
                new UOM("5", "BILLIONS OF UNITS", "Measure", "BOU"),
                new UOM("6", "BOX", "Measure", "BOX"),
                new UOM("7", "BOTTLES", "Measure", "BTL"),
                new UOM("8", "BUNCHES", "Measure", "BUN"),
                new UOM("9", "CANS", "Measure", "CAN"),
                new UOM("10", "CUBIC METER", "Volume", "CBM"),
                new UOM("11", "CUBIC CENTIMETER", "Volume", "CCM"),
                new UOM("12", "CENTIMETER", "Length", "CMS"),
                new UOM("13", "CARTONS", "Measure", "CTN"),
                new UOM("14", "DOZEN", "Measure", "DOZ"),
                new UOM("15", "DRUM", "Measure", "DRM"),
                new UOM("16", "GREAT GROSS", "Measure", "GGR"),
                new UOM("17", "GRAMS", "Weight", "GMS"),
                new UOM("18", "GROSS", "Measure", "GRS"),
                new UOM("19", "GROSS YARDS", "Length", "GYD"),
                new UOM("20", "KILOGRAMS", "Weight", "KGS"),
                new UOM("21", "KILOLITER", "Volume", "KLR"),
                new UOM("22", "KILOMETRE", "Length", "KME"),
                new UOM("23", "MILLILITRE", "Volume", "MLT"),
                new UOM("24", "METERS", "Length", "MTR"),
                new UOM("25", "METRIC TONS", "Weight", "MTS"),
                new UOM("26", "NUMBERS", "Measure", "NOS"),
                new UOM("27", "PACKS", "Measure", "PAC"),
                new UOM("28", "PIECES", "Measure", "PCS"),
                new UOM("29", "PAIRS", "Measure", "PRS"),
                new UOM("30", "QUINTAL", "Weight", "QTL"),
                new UOM("31", "ROLLS", "Measure", "ROL"),
                new UOM("32", "SETS", "Measure", "SET"),
                new UOM("33", "SQUARE FEET", "Area", "SQF"),
                new UOM("34", "SQUARE METERS", "Area", "SQM"),
                new UOM("35", "SQUARE YARDS", "Area", "SQY"),
                new UOM("36", "TABLETS", "Measure", "TBS"),
                new UOM("37", "TEN GROSS", "Measure", "TGM"),
                new UOM("38", "THOUSANDS", "Measure", "THD"),
                new UOM("39", "TONNES", "Weight", "TON"),
                new UOM("40", "TUBES", "Measure", "TUB"),
                new UOM("41", "US GALLONS", "Volume", "UGS"),
                new UOM("42", "UNITS", "Measure", "UNT"),
                new UOM("43", "YARDS", "Length", "YDS"),
                new UOM("44", "OTHERS", "", "OTH")
        );

        return UOMList;
    }

}
