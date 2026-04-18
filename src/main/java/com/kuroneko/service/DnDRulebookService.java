package com.kuroneko.service;

import com.kuroneko.database.entity.DnDSpellEntity;
import com.kuroneko.database.mapper.DnDSpellMapper;
import com.kuroneko.database.repository.DnDSpellRepository;
import com.kuroneko.misc.SpellDetailsResponse;
import com.kuroneko.misc.SpellListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class DnDRulebookService {

    private final DnDSpellRepository spellRepository;
    private final DnDSpellRepository dnDSpellRepository;
    private final RestClient restClient;


    public DnDRulebookService(DnDSpellRepository spellRepository,
                              DnDSpellRepository dnDSpellRepository,
                              RestClient.Builder restClientBuilder) {
        this.spellRepository = spellRepository;
        this.dnDSpellRepository = dnDSpellRepository;
        this.restClient = restClientBuilder.build();
    }

    public List<DnDSpellEntity> findSpells(String name) {
        return spellRepository.findAllByNameContainingIgnoreCase(name);
    }

    public DnDSpellEntity fetchFullSpell(DnDSpellEntity spellEntity) {

        if (!spellEntity.isDownloaded()) {
            SpellDetailsResponse spellDetails = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("www.dnd5eapi.co")
                            .path("/api/spells/{index}")
                            .build(spellEntity.getIndex()))
                    .retrieve()
                    .body(SpellDetailsResponse.class);

            if (spellDetails == null) {
                throw new RuntimeException("Failed to fetch spell details from the API");
            } else {
                log.info("Fetched {} details from the API", spellEntity.getIndex());
            }
            return dnDSpellRepository.save(DnDSpellMapper.mapDetails(spellEntity, spellDetails));
        }

        return spellEntity;
    }

    public List<DnDSpellEntity> findAllSpells() {
        return spellRepository.findAll();
    }

    public void initializeSpells() {
        List<DnDSpellEntity> alreadyFetched = spellRepository.findAll();
        SpellListResponse spellListResponse = fetchSpellList();
        if (alreadyFetched.isEmpty()) {
            saveFetchedSpells(spellListResponse.getResults());
        } else if (alreadyFetched.size() == spellListResponse.getCount()) {
            log.info("Spells were already fetched");
        } else if (alreadyFetched.size() < spellListResponse.getCount()) {
            log.info("New Spells found, saving...");
            List<SpellListResponse.SpellResult> fetchedDiff = spellListResponse.getResults().stream()
                    .filter(s -> alreadyFetched.stream()
                            .noneMatch(f -> f.getIndex().equals(s.getIndex())))
                    .toList();
            saveFetchedSpells(fetchedDiff);
            log.info("...spells saved");
        }
    }

    private void saveFetchedSpells(List<SpellListResponse.SpellResult> spellResults) {
        List<DnDSpellEntity> newEntities = spellResults.stream()
                .map(DnDSpellMapper::map)
                .toList();
        spellRepository.saveAll(newEntities);
    }

    private SpellListResponse fetchSpellList() {

        SpellListResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("www.dnd5eapi.co")
                        .path("/api/spells")
                        .build()
                ).retrieve()
                .body(SpellListResponse.class);

        if (response == null) {
            throw new RuntimeException("Failed to fetch spells from the API");
        } else {
            log.info("Fetched {}/{} spells from the API", response.getResults().size(), response.getCount());
        }

        return response;
    }
}
