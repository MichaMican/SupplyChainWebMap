import mapboxgl, { GeoJSONSourceRaw } from "mapbox-gl";
import mapTilerKey from "./mapTilerKey";

const style = getOsmVectorTilesStyle();
const map = new mapboxgl.Map({
    container: "map",
    style: style
});

map.on("load", () =>{
    addPoint(map);
});

function getOsmVectorTilesStyle(): string {
    return `https://api.maptiler.com/maps/0469957e-f047-42d1-8c79-61d05a561b57/style.json?key=${mapTilerKey}`;
}

function getOsmRasterTilesStyle(): mapboxgl.Style {
    return {
        "version": 8,
        "sources": {
            "osm": {
                "type": "raster",
                "tiles": [
                    "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                ],
                "tileSize": 256
            }
        },
        "layers": [
            {
                "id": "osm",
                "type": "raster",
                "source": "osm"
            }
        ]
    };
}

function addPoint(map: mapboxgl.Map) {
    const geoJsonSource: GeoJSONSourceRaw = {
        "type": "geojson",
        "data": {
            "type": "Feature",
            "properties": {
                "type": "lithium"
            },
            "geometry": {
                "type": "Point",
                "coordinates": [12.954884, 48.829651]
            }
        }
    };

    map.addSource("points", geoJsonSource);

    map.addLayer({
        "id": "points",
        "type": "circle",
        "source": "points",
        "paint": {
            "circle-radius": 5,
            "circle-color": "#3D33FF"
        }
    });
}