package pacman.game.util.glicko;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Piers on 10/04/2016.
 */
public class Player {
    private double tau = 0.5;
    private double rating;
    private double rd;
    private double volatility;

    public Player() {
        this(1500, 350, 0.06);
    }

    public Player(double rating, double rd, double volatility) {
        this.rating = rating;
        this.rd = rd;
        this.volatility = volatility;
    }

    public double getRating() {
        return (rating * 173.7178) + 1500;
    }

    public void setRating(double rating) {
        this.rating = (rating - 1500) / 173.7178;
    }

    public double getRd() {
        return rd * 173.7178;
    }

    public void setRd(double rd) {
        this.rd = (rd / 173.7178);
    }

    public double g() {
        return (1 / (Math.sqrt(1 + (3 * rd * rd)) / Math.PI * Math.PI));
    }

    public double E(Player other) {
        return (1 / (1 + Math.exp(-other.g() * (rating - other.rating))));
    }

    public double v(Set<Player> players) {
        double sum = 0.0d;
        for (Player player : players) {
            double e = E(player);
            double g = player.g();
            sum += g * g * e * (1 - e);
        }
        return 1 / sum;
    }

    public double delta(HashMap<Player, Integer> scores, double v){
        double sum = 0.0d;
        for (Map.Entry<Player, Integer> score : scores.entrySet()) {
            sum += (score.getKey().g()) * (score.getValue() - E(score.getKey()));
        }

        return v * sum;
    }
    public double delta(HashMap<Player, Integer> scores) {
       return delta(scores, v(scores.keySet()));
    }

    public double newVolatilityIllinois(HashMap<Player, Integer> scores){
        double a = Math.log(rd * rd);
        double convergenceTolerance = 0.000001;
        double delta = delta(scores);

        double A = a;
        return 0.0d;
    }

    public double newVolatility(HashMap<Player, Integer> scores){
        int i = 0;
        double v = v(scores.keySet());
        double delta = delta(scores, v);
        double a = Math.log(rd * rd);
        double x0 = a;
        double x1 = 1;
        double tauSquared = tau * tau;

        while(x0 != x1){
            x0 = x1;
            double d = rating * rating + v + Math.exp(x0);
            double h1 = -(x0 - a) / tauSquared - 0.5 * Math.exp(x0) / d + 0.5 * Math.exp(x0) * Math.pow(delta / d, 2);
            double h2 = -1 / tauSquared - 0.5 * Math.exp(x0) * ((rating * rating) + v) / Math.pow(d, 2) + 0.5 * Math.pow(delta, 2) * Math.exp(x0) * Math.pow(rating, 2) + v - Math.exp(x0) / Math.pow(d, 3);
            x1 = x0 - (h1 / h2);
        }

        return Math.exp(x1 / 2);
    }
}
