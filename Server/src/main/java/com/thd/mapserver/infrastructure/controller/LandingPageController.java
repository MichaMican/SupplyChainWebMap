package com.thd.mapserver.infrastructure.controller;

import java.util.ArrayList;

import com.thd.mapserver.postsql.PostgresqlPoiRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

	PostgresqlPoiRepository dbConnect = new PostgresqlPoiRepository();

	@GetMapping("/")
	public String test(Model model) {
		var collections = new ArrayList<FeatureCollection>();

		var collectionsRaw = dbConnect.getAllCollections();
		for (var collection:collectionsRaw) {
			collections.add(new FeatureCollection(collection.title, collection.description, "/collections/"+collection.typ));
		}

		model.addAttribute("collections", collections);
		return "index";
	}

	@GetMapping("/conformance")
	public String getLandingPage() {
		return "conformance";
	}
	
	public static class FeatureCollection {
		private final String name;
		private final String description;
		private final String href;

		public FeatureCollection(String name, String description, String href) {
			this.name = name;
			this.description = description;
			this.href = href;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getDescription() {
			return this.description;
		}

		public String getHref() {
			return href;
		}
	}

}
