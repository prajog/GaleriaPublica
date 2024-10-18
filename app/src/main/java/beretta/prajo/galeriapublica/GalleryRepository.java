package beretta.prajo.galeriapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.view.menu.ListMenuItemView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beretta.prajo.galeriapublica.util.Util;

public class GalleryRepository {
    Context context; //objeto do tipo context, para que seja possivel acessar o conteudo da galeria do celular

    public GalleryRepository(Context context){
        this.context = context;
    }

    //parametros: limit (numero de elementos que devem ser carregados, offset (indice a partir do qual os elementos devem ser carregados
    //retorna uma lista de objetos ImageData, com somente a quantidade de itens referentes a uma pagina
    public List<ImageData> loadImageData(Integer limit, Integer offSet) throws FileNotFoundException {
        List<ImageData> imageDataList = new ArrayList<>(); //lista de ImageData
        //pega a dimensao que cada miniatura de foto deve ter
        int w = (int) context.getResources().getDimension(R.dimen.im_width);
        int h = (int) context.getResources().getDimension(R.dimen.im_height);

        //acessa a tabela que guarda as imagens no espaco publico do celular como um banco de dados e obtem as colunas
        String[] projection = new String[] {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE}; //size: tamanho do arq em bytes, display_name: nome do arq de foto, id: usado para construir o uri
        String selection = null; //define um subconjunto dos dados, eh nulo pq nao queremos apenas mas todas as fotos
        String selectionArgs[] = null; //define os argumentos para o selection (eh nulo pq o selection tbm eh
        String sort = MediaStore.Images.Media.DATE_ADDED; //define a coluna que sera usada para ordenar os resultados da pesquisa, aqui eh por data de criacao

        //executa a query ao bd de imagens do celular
        Cursor cursor = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q){
            //para versoes posteriores ao android 11, os paramentos da consulta passam por bundle
            Bundle queryArgs = new Bundle(); //bundle guarda tuplas do tipo chave=valor
            //definindo os parametros de consulta:
            //define os parametros de selection e selectionargs
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
            //define as colunas que ordenarao o resultado da consulta
            queryArgs.putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, sort);
            //define a direcao da ordenacao (asc ou desc)
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION, ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);
            // indica os parametros limit e offset
            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
            queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offSet);

            //realiza a consulta pelo ContentResolver (obtido do contexto da aplicacao). O resultado retorna em um objeto do tipo cursor
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, queryArgs, null);
        }
        else{
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sort + " ASC + LIMIT " + String.valueOf(limit) + " OFFSET " + String.valueOf(offSet));
        }

        //obtendo os dados para as fotos
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

        while (cursor.moveToNext()){
            //Pega os valores das colunas de uma determinada imagem
            long id = cursor.getLong(idColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            String name = cursor.getString(nameColumn);
            int dateAdded = cursor.getInt(dateAddedColumn);
            int size = cursor.getInt(sizeColumn);
            Bitmap thumb = Util.getBitmap(context, contentUri, w, h); //cria miniatura da foto

            //Stores column values and the contentUri in a local object that represents the media file
            //cria um objeto do tipo ImageData e adiciona na lista de ImageData
            imageDataList.add(new ImageData(contentUri, thumb, name, new Date(dateAdded*1000L), size));
        }
        return imageDataList; //retorna lista de ImageData com apenas a qtd de itens referentes a uma pagina de dados
    }
}
