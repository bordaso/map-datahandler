package ec33nw.map.datahandler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ec33nw.map.datahandler.entity.Tutorial;

public interface ITutorialRepository extends JpaRepository<Tutorial, Long> {
	  List<Tutorial> findByPublished(boolean published);
	  List<Tutorial> findByTitleContaining(String title);
	}
