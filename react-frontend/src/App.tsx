import React from 'react';
import './App.css';
import './mapbox.css';
import mapboxgl, { GeoJSONSourceRaw } from 'mapbox-gl';
import Settings from './settings';
import Select, { ValueType } from 'react-select';
import axios from "axios";
import { getColorCode, colourStyles } from "./colors";
import { isNullOrUndefined } from 'util';


type OptionType = {
  value: string;
  label: string;
  description: string;
};

type Bbox = {
  x1: number;
  y1: number;
  x2: number;
  y2: number;
}

interface AppState {
  optionsToSelect: OptionType[],
  map: mapboxgl.Map | undefined,
  lng: number | undefined,
  lat: number | undefined,
  zoom: number | undefined,
  lastSelection: OptionType[],
  currentlySelected: OptionType[],
  mySourceIds: string[],
  bbox: Bbox | undefined
}

class App extends React.Component<any, AppState> {

  constructor(props: any) {
    super(props);

    this.state = {
      optionsToSelect: [],
      map: undefined,
      lng: undefined,
      lat: undefined,
      zoom: undefined,
      lastSelection: [],
      mySourceIds: [],
      currentlySelected: [],
      bbox: undefined
    }

  }


  componentDidMount() {

    let map = new mapboxgl.Map({
      container: "map",
      style: `https://api.maptiler.com/maps/${Settings.clientId}/style.json?key=${Settings.mapTilerKey}`
    });

    map.on('move', () => {
      this.setState({
        lng: map.getCenter().lng,
        lat: map.getCenter().lat,
        zoom: map.getZoom()
      });
    });

    this.setState({
      map
    })

    axios
      .get(`http://localhost:8080/collections/`)
      .then((response) => {
        if (response.status >= 200 && response.status < 300) {

          var newSelectOptions: OptionType[] = [];

          response.data.collections.forEach((e: any) => {
            newSelectOptions.push({ label: e.title, value: e.id, description: e.description })
          })

          this.setState({
            optionsToSelect: newSelectOptions
          })
        } else {
          console.error({ status: response.status, statusText: response.statusText });
        }
      })
      .catch((error: Error) => {
        console.error({ status: 400, statusText: error.message });
      });
  }

  private triggerPointPopUp = (e: any) => {
    var coordinates = e.features[0].geometry.coordinates.slice();
    var description = e.features[0].properties.description;
    var title = e.features[0].properties.title;
    // Ensure that if the map is zoomed out such that multiple
    // copies of the feature are visible, the popup appears
    // over the copy being pointed to.
    if (Array.isArray(coordinates[0])) {
      coordinates = null
    }

    if (coordinates) {
      while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
        coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
      }

      if (this.state.map) {
        new mapboxgl.Popup()
          .setLngLat(coordinates)
          .setHTML(`<div><b>${title}</b><br>${description}</div>`)
          .addTo(this.state.map);
      } else {
        console.error("Popup couldn't be shown because the map dissapeared")
      }
    }
  }

  private changeMouseStylePointer = () => {
    (this.state.map as mapboxgl.Map).getCanvas().style.cursor = 'pointer';
  }

  private changeMouseStyleNone = () => {
    (this.state.map as mapboxgl.Map).getCanvas().style.cursor = '';
  }

  handleChange = (selectedOptions: ValueType<OptionType>): void => {
    if (selectedOptions) {
      let options = selectedOptions as OptionType[];
      this.setState({
        currentlySelected: options
      })
      options.forEach((option: OptionType) => {
        if (this.state.map) {
          if (!this.state.map.getSource(option.value)) {

            let url: string = `http://localhost:8080/collections/${option.value}/items.json?limit=100`

            if (this.state.bbox) {
              url += `&bbox=${this.state.bbox.x1},${this.state.bbox.y1},${this.state.bbox.x2},${this.state.bbox.y2}`
            }

            axios.get(url).then((response) => {
              if (response.status >= 200 && response.status < 300) {
                const geoJsonSource: GeoJSONSourceRaw = {
                  "type": "geojson",
                  "data": response.data
                };

                let mySourceIds = this.state.mySourceIds;

                this.state.map?.addSource(option.value, geoJsonSource)
                mySourceIds.push(option.value)

                this.state.map?.addLayer({
                  'id': `${option.value}-Polygons`,
                  'type': 'fill',
                  'source': option.value,
                  'paint': {
                    'fill-color': getColorCode(option.value),
                    'fill-opacity': 0.4
                  },
                });

                this.state.map?.addLayer({
                  'id': `${option.value}-Points`,
                  'type': 'circle',
                  'source': option.value,
                  'paint': {
                    'circle-radius': 6,
                    'circle-color': getColorCode(option.value),
                    'circle-stroke-color': '#000000',
                    'circle-stroke-width': 2
                  },
                });

                this.state.map?.on('click', `${option.value}-Points`, this.triggerPointPopUp);
                this.state.map?.on('mouseenter', `${option.value}-Points`, this.changeMouseStylePointer);
                this.state.map?.on('mouseleave', `${option.value}-Points`, this.changeMouseStyleNone);
              } else {
                console.error({ status: response.status, statusText: response.statusText });
              }
            })
          } else {
            this.showCollection(option.value);
          }
        } else {
          console.error("Map is not initialized yet")
        }
      })

      this.state.lastSelection.forEach((e: OptionType) => {
        if (!options.includes(e)) {
          this.hideCollection(e.value)
        }
      })

      this.setState({
        lastSelection: options
      })
    } else {
      this.state.lastSelection.forEach((e: OptionType) => {
        this.hideCollection(e.value)
      })

      this.setState({
        currentlySelected: []
      })
    }
  }

  private hideCollection(collectionId: string) {
    if (this.state.map) {
      if (this.doesLayerExist(`${collectionId}-Polygons`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Polygons`, 'visibility', 'none');
      }
      if (this.doesLayerExist(`${collectionId}-Points`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Points`, 'visibility', 'none');
      }
    }
  }

  private showCollection(collectionId: string) {
    if (this.state.map) {
      if (this.doesLayerExist(`${collectionId}-Polygons`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Polygons`, 'visibility', 'visible');
      }
      if (this.doesLayerExist(`${collectionId}-Points`)) {
        this.state.map.setLayoutProperty(`${collectionId}-Points`, 'visibility', 'visible');
      }
    }
  }

  private doesLayerExist = (layerId: string): boolean => {
    return !isNullOrUndefined(this.state.map?.getLayer(layerId))
  }

  reset = () => {
    this.setState({
      lastSelection: [],
      currentlySelected: []
    })

    this.state.mySourceIds.forEach(element => {
      if (this.state.map?.getSource(element)) {
        if (this.state.map?.getLayer(`${element}-Polygons`)) {
          this.state.map?.removeLayer(`${element}-Polygons`);
        }
        if (this.state.map?.getLayer(`${element}-Points`)) {
          this.state.map?.removeLayer(`${element}-Points`);
          this.state.map?.off('click', `${element}-Points`, this.triggerPointPopUp);
          this.state.map?.off('mouseenter', `${element}-Points`, this.changeMouseStylePointer);
          this.state.map?.off('mouseleave', `${element}-Points`, this.changeMouseStyleNone);
        }
        this.state.map?.removeSource(element);
      }
    })

  }

  handleSetBboxButton = () => {
    if (this.state.map) {
      let bounds = this.state.map.getBounds();
      this.setState({
        bbox: {
          x1: bounds.getSouthWest().lng,
          y1: bounds.getSouthWest().lat,
          x2: bounds.getNorthEast().lng,
          y2: bounds.getNorthEast().lat,
        }
      }, () => {
        let currentSelection = this.state.currentlySelected;
        this.reset();
        this.setState({
          currentlySelected: currentSelection
        })
        this.handleChange(currentSelection)
      })
    }
  }

  handleResetBboxButton = () => {
    if (this.state.map) {
      this.setState({
        bbox: undefined
      }, () => {
        let currentSelection = this.state.currentlySelected;
        this.reset();
        this.setState({
          currentlySelected: currentSelection
        })
        this.handleChange(currentSelection)
      })
    }
  }

  getCollectionsSidebar = (currentSelection: OptionType[]) => {
    return currentSelection.map((collection: OptionType) => {
      return (
        <div>
          <div>
            <i className="material-icons" style={{ cursor: "pointer", float: "right" }} onClick={() => {
              window.open(`https://google.com/search?q=${encodeURI(collection.label)}`)
            }}>help_outline</i>
            <h3 style={{ marginBottom: 2, color: getColorCode(collection.value) }}>{collection.label}</h3>
          </div>
          <div>{collection.description}</div>
        </div>

      )
    })
  }

  render() {
    return (
      <div>
        <div className="overlay">
          <Select
            options={this.state.optionsToSelect}
            isMulti
            onChange={option => this.handleChange(option)}
            placeholder="Select Collections"
            styles={colourStyles}
            value={this.state.currentlySelected}
          />
          <button onClick={this.handleSetBboxButton}>Set bbox</button>
          <button onClick={this.handleResetBboxButton}>Reset bbox</button>
          <button onClick={this.reset}>Reset</button>
          {
            this.state.bbox &&
            <div className="bboxInfo">
              {`Bbox set (x1: ${this.state.bbox.x1}, y1: ${this.state.bbox.y1}, x2: ${this.state.bbox.x2}, y2: ${this.state.bbox.y2})`}
            </div>
          }
        </div>
        <div id="map" style={this.state.currentlySelected.length > 0 ? { width: "calc(100% - 350px)" } : {}} />
        {this.state.currentlySelected.length > 0 &&
          (
            <div className="sidebar">
              <h1>Collections</h1>
              <hr />
              <div style={{ textAlign: "left", padding: 10 }}>
                {this.getCollectionsSidebar(this.state.currentlySelected)}
              </div>
            </div>
          )
        }
      </div>
    );
  }

}

export default App;
