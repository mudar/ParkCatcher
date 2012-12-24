[![Park Catcher / Capteur de stationnement][img_github]][link_parkcatcher]

##A Montréal Open Data project. Find a free parking in the nearest residential area.

Park Catcher Montréal is your guide to easy parking. Find spots where parking is allowed for the time period you need, using the interactive map and the search engine. Stop driving in circles, avoid parking tickets, head directly to the right street! 

Powered by Open Data provided by the City of Montréal [Open Data Portal][link_portal], Park Catcher is an open source [website][link_parkcatcher] and [Android app][link_parkcatcher_playstore].

A **GeoJSON API** is also available. Check the [Wiki][link_wiki_api] or the [example][link_api_example] for more information. **iOS** mobile version is currently under development…


##Links

[www.parkcatcher.com][link_parkcatcher]

[www.capteurdestationnement.com][link_capteurdestationnement]

[Park Catcher on Google Play][link_parkcatcher_playstore]

[![Android app on Google Play][img_playstore_badge]][link_parkcatcher_playstore]

Check the project's [Wiki][link_wiki] for more resources:  

* Fully-documented [**GeoJSON API**][link_wiki_api].
* [**How Does It Work?**][link_wiki_how_does_it_work]

##Open Data

This project was done in collaboration with [Montréal Ouvert][link_mtl_ouvert].

[![Montréal ouvert][img_mtl_ouvert]][link_mtl_ouvert]

Data sources are JSON and KMZ files provided by the City of Montréal:

1. [KMZ][link_portal_1]: Parking rules grouped by borough.
2. [JSON][link_portal_2]: Parking rules grouped by type.

[Open Data License][link_portal_license] of the City of Montréal.

## Credits

* Developed by [Mudar Noufal][link_mudar_ca]  &lt;<mn@mudar.ca>&gt;
* Art Direction and Graphic Design by [Emma Dumesnil][link_emma_cargo]

##Code license

    Park Catcher Montréal
    Copyright (C) 2012  Mudar Noufal <mn@mudar.ca>

    Find a free parking in the nearest residential street when driving in
    Montréal. A Montréal Open Data project.

    This file is part of Park Catcher Montréal.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.


The **website** is powered by [CodeIgniter][link_lib_codeigniter] &copy; EllisLab, Inc. Map data &copy; 2012 [OpenStreetMap][link_lib_openstreetmap], powered by [Leaflet][link_lib_leaflet] &copy; CloudMade.

The **Android app** includes (thanks!) libraries and derivative work of the following projects:

* [Action Bar Sherlock][link_lib_actionbarsherlock] &copy; Jake Wharton.
* [Google Gson][link_lib_gson] &copy; Google Inc.
* [Google I/O App][link_lib_iosched] iosched &copy; Google Inc.
* [Android Protips Location][link_lib_radioactiveyak] &copy; Reto Meier
* [AOSP][link_lib_aosp] &copy; The Android Open Source Project.

These five projects are all released under the [Apache License v2.0][link_apache]. Map data is powered by Google Maps Android API v2.

[![Android app on Google Play][img_devices]][link_parkcatcher_playstore]

[link_capteurdestationnement]: http://www.capteurdestationnement.com/
[link_parkcatcher]: http://www.parkcatcher.com/
[link_parkcatcher_playstore]: http://play.google.com/store/apps/details?id=ca.mudar.parkcatcher
[link_mtl_ouvert]: http://montrealouvert.net/?page_id=113&lang=en
[link_mudar_ca]: http://www.mudar.ca/
[link_emma_cargo]: http://cargocollective.com/emmadumesnil/
[link_wiki]: https://github.com/mudar/ParkCatcher/wiki/
[link_wiki_api]: https://github.com/mudar/ParkCatcher/wiki/API
[link_wiki_how_does_it_work]: https://github.com/mudar/ParkCatcher/wiki/How-Does-It-Work%3F
[link_api_example]: http://www.parkcatcher.com/api/?day=5&hour=10.5&duration=2&destination=h3c2n5&radius=500&latNW=45.500564421707544&lonNW=-73.56202840805054&latSE=45.49463845610178&lonSE=-73.54838132858276
[link_gpl]: http://www.gnu.org/licenses/gpl.html
[link_portal]: http://donnees.ville.montreal.qc.ca/
[link_portal_1]: http://donnees.ville.montreal.qc.ca/fiche/stationnement-rue/
[link_portal_2]: http://depot.ville.montreal.qc.ca/stationnement-rue/zz-json/data.zip
[link_portal_license]: http://donnees.ville.montreal.qc.ca/licence-texte-complet
[link_apache]: http://www.apache.org/licenses/LICENSE-2.0
[img_mtl_ouvert]: http://www.parkcatcher.com/img/logo_montreal_ouvert_github.png
[img_github]: http://www.parkcatcher.com/img/park-catcher-github.png
[img_devices]: http://www.parkcatcher.com/img/phone_tablet_github.png
[img_playstore_badge]: http://developer.android.com/images/brand/en_app_rgb_wo_60.png
[link_lib_codeigniter]: http://ellislab.com/codeigniter
[link_lib_leaflet]: http://leafletjs.com/
[link_lib_openstreetmap]: http://www.openstreetmap.org/
[link_lib_actionbarsherlock]: https://github.com/JakeWharton/ActionBarSherlock
[link_lib_gson]: http://code.google.com/p/google-gson/
[link_lib_iosched]: http://code.google.com/p/iosched/
[link_lib_radioactiveyak]: http://code.google.com/p/android-protips-location/
[link_lib_aosp]: http://source.android.com/
[link_apache]: http://www.apache.org/licenses/LICENSE-2.0

