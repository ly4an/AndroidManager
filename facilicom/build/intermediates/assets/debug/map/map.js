function Main () {
    var Lng = $('#Lng').val();
    var Lat = $('#Lat').val();

    $('#LngText').text(Lng);
    $('#LatText').text(Lat);

    var DataTable = $('#DataTable');
    var DataTableHeight = DataTable.outerHeight(true);

    var MaxWidth = 600;
    var MaxHeight = 450;

    var Window = $(window);

    var Width = Window.width();
    var Height = Window.height() - DataTableHeight;

    if(Width > MaxWidth) {
        Width = MaxWidth;
    }

    if(Height > MaxHeight)
    {
        Height = MaxHeight;
    }

    var CenterLng = Math.round(Lng * 10000) / 10000;
    var CenterLat = Math.round(Lat * 10000) / 10000;

    var Query = ['http://static-maps.yandex.ru/1.x/?l=map&ll=', CenterLng, ',', CenterLat, '&z=16&size=', Width, ',', Height, '&pt=', CenterLng, ',', CenterLat, ',pm2rdl'].join('');

    var MapImage = $('#MapImage');

    MapImage.load(function () {
        DataTable.css('visibility', 'visible');
    });

    MapImage.error(function () {
        MapImage.prop('src', 'NoImage.jpg');
    });

    MapImage.prop('src', Query);
}
