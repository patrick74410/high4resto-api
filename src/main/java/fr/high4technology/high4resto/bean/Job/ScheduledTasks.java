package fr.high4technology.high4resto.bean.Job;

import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import fr.high4technology.high4resto.Util.Util;
import fr.high4technology.high4resto.bean.Client.ClientRepository;
import fr.high4technology.high4resto.bean.Stock.StockRepository;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrder;
import fr.high4technology.high4resto.bean.Tracability.PreOrder.PreOrderRepository;
import fr.high4technology.high4resto.bean.ItemCarte.ItemCarte;

import reactor.core.publisher.Flux;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {

        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

    @Autowired
    private ClientRepository clients;
    @Autowired
    private PreOrderRepository preorders;
    @Autowired
    private StockRepository stocks;


    @Scheduled(fixedRate = 1000*600 )
    public void reportCurrentTime() {
        Queue<PreOrder> globalQueue = new ConcurrentLinkedQueue<PreOrder>();
        var flux = clients.findAll().map(client->{
            ArrayList<ItemCarte> items= new ArrayList<ItemCarte>();
            ArrayList<PreOrder> preO=new ArrayList<PreOrder>();
            for(PreOrder item:client.getCommande().getItems())
            {
                try
                {
                    Date dateNow=Util.getDateNow();
                    Date dateItem=Util.parseDate(item.getInside());
                    if(getDateDiff(dateItem,dateNow,TimeUnit.MINUTES)>15)
                    {
                        // add to basket
                        items.add(item.getStock().getItem());
                        // add to preOrdersRemove
                        globalQueue.add(item);
                    }
                    else
                    {
                        preO.add(item);
                    }
                }
                catch(Exception e)
                {
                    log.info(e.getMessage());
                }
            }

            client.getCurrentPanier().addAll(items);
            client.getCommande().setItems(preO);
            return client;
        }).flatMap(clients::save)
        .collectList()
        .thenMany(Flux.fromIterable(globalQueue)
        .flatMap(preOrder->{
            return stocks.save(preOrder.getStock());
        })
        .flatMap(stock->{
            return preorders.deleteById(stock.getId());
        }));

        flux.doOnSubscribe(data -> log.info("data:" + data)).thenMany(flux).subscribe(
                                data -> log.info("data:" + data), err -> log.error("error:" + err),
                                () -> log.info("done initialization..."));
	}
}
